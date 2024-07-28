# Contributing to Project

## Branching
- **Do not commit directly to the "main" branch. Only merge when the code works**
  - The "main" branch should always be in a deployable state and contain tested, production-ready code.

- **Create a new branch for each feature or bug fix.**

- **Only branch off of the "main" branch, unless there is a specific reason.**
  - This helps keep the workflow simple and ensures that everyone is working from the same base code.
  - Use the following commands to create and switch to a new branch while in the "main" branch:
    ```sh
    git checkout main  # Ensure you are on the main branch
    git checkout -b <branch_name>  # Replace <branch_name> with your branch name
    ```