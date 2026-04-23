These rules override everything else. Follow them strictly on every task:

## Exploration — Tool-Based, Not Shell-Based

- **No shell exploration commands.** Never use `ls`, `ls -R`, `cat`, `find`, `tree`, or any bash/shell command to explore the project structure. Use dedicated file tools instead:
  - **`list_dir`** — to get a one-shot overview of a directory's contents.
  - **`view_file`** — to read a specific file directly by path.
  - **`grep_search`** — to locate a class, method, annotation, or pattern across the codebase without opening unnecessary files.
- **One structural scan only.** Call `list_dir` on the project root at most once per task to understand the layout. Do not scan recursively into every subdirectory.

## Reading Files — Targeted, Not Broad

- **Targeted reads only.** Before reading any file, ask: *"Is this file directly required to complete the task?"* If no → skip it.
- **Layer discipline.** Read only files in the relevant layer:
  - Service task → read only Service + relevant Repository/Entity/DTO files.
  - Controller task → read only Controller + Request/Response DTOs.
  - Never re-read the full Backend when the API contract is already stable.
- **Parallel reads.** When multiple files are needed, read them in one parallel batch — not one by one sequentially.
- **grep before open.** Use `grep_search` to find the exact class/method/field first. Only then open the file with `view_file`.
- **No blind full-file reads.** For large files, read only the relevant line range using `view_file` with `StartLine`/`EndLine`.

## Coding — Act Immediately

- **Code immediately.** Once you have enough context, write or edit code directly. Do not produce intermediate analysis, exploration notes, or summaries unless asked.
- **No summary files.** Never create `analysis.md`, `overview.md`, `notes.md`, or any exploratory document. If planning is needed, follow Section 2 of CLAUDE.md.
- **No narration.** Do not narrate your steps (e.g., "Now I will read the controller…"). Just act silently and produce results.
- **Minimal tool calls.** Consolidate reads. Prefer reading multiple related files in one pass over making repeated individual calls.

## Commands — User Owns Build & Run

- **No build or start commands.** Never run `mvn`, `gradle`, `./gradlew`, `npm run`, `java -jar`, or any build/start/test command. The user handles all build, run, and test steps themselves.
- **No shell side-effects.** Do not run any command that mutates state, installs dependencies, or starts servers.