package learning.tasknode.service;

import learning.tasknode.dto.response.TaskAttachmentResponse;
import learning.tasknode.entity.Task;
import learning.tasknode.entity.TaskAttachment;
import learning.tasknode.exception.FileStorageException;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.mapper.TaskAttachmentMapper;
import learning.tasknode.repository.TaskAttachmentRepository;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskAttachmentService {
    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAttachmentMapper attachmentMapper;

    @Value("${attachment.upload-dir:uploads}")
    private String uploadDir;

    public List<TaskAttachmentResponse> listByTask(Long taskId) {
        return attachmentRepository.findByTaskIdAndIsDeletedFalse(taskId).stream()
                .map(attachmentMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public TaskAttachmentResponse uploadAttachment(Long taskId, MultipartFile file, String uploader) throws IOException {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc phù hợp!"));

        String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!ext.equals("pdf") && !ext.equals("doc") && !ext.equals("docx")) {
            throw new IllegalArgumentException("Chỉ cho phép file Word hoặc PDF (.doc, .docx, .pdf)!");
        }
        String uuid = UUID.randomUUID().toString();
        String fname = uuid + "_" + file.getOriginalFilename();
        Path dir = Paths.get(uploadDir, "task-" + taskId);
        Files.createDirectories(dir);
        Path savePath = dir.resolve(fname);
        try {
            file.transferTo(savePath);
        } catch (IOException e) {
            throw new FileStorageException("Lưu file thất bại, vui lòng thử lại!", e);
        }

        learning.tasknode.entity.User uploadedByUser = userRepository.findByUsername(uploader)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người upload: " + uploader));

        TaskAttachment attachment = TaskAttachment.builder()
                .task(task)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .fileUrl(savePath.toString().replace("\\", "/"))
                .uploadedBy(uploadedByUser)
                .build();
        attachment = attachmentRepository.save(attachment);
        return attachmentMapper.toResponse(attachment);
    }

    @Transactional
    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment att = attachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu đính kèm!"));
        att.setIsDeleted(true);
        attachmentRepository.save(att);
        // Xóa file vật lý nếu tồn tại
        File f = new File(att.getFileUrl());
        if (f.exists()) {
            f.delete();
        }
    }
}
