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

## Workflow

### Start of Work
- **Fetch (If you are in the middle of something and don't want to mess up the work directory) or pull the latest changes:**
  - Ensure your local repository is up-to-date:
    ```sh
    git pull origin main
    # or
    git fetch origin main
    ```

### End of Work
- **Fetch (To review the updated files) or pull (Less control over the merge process) the latest changes again:**
  - Ensure you have the most recent updates before merging:
    ```sh
    git fetch origin main
    # or
    git pull origin main 
    ```
- **Review the fetched changes (Only for fetch):**
  - View the new commits:
    ```sh
    git log HEAD..origin/main
    ```
  - Check the differences:
    ```sh
    git diff HEAD..origin/main
    ```
  - List the files changed:
    ```sh
    git diff --name-only HEAD..origin/main
    ```

- **Merge the latest changes (Only for fetch):**
  - Example:
    ```sh
    git merge origin/main
    ```
- **Resolve any conflicts that arise.**

- **Push your changes to the remote repository:**
  - Example:
    ```sh
    git push -u origin <branch_name>
    ```

## Finished Work
- **Push to the "main" branch when everything works perfectly.**