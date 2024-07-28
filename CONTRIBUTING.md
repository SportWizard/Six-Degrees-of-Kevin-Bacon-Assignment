# Contributing to Project

**Tutorial (Devin Lo):** https://docs.google.com/document/d/12TmFKePVelHxftiEZ_8il80g4cj-IeXMhKWBEY1GBWU
## Branching
- **Do not commit directly to the "main" branch. Only merge when the code works**
  - The "main" branch should always be in a deployable state and contain tested, production-ready code.

- **Create a new branch for each feature or bug fix.**

- **Only branch off of the "main" branch, unless there is a specific reason.**
  - This helps keep the workflow simple and ensures that everyone is working from the same base code.
  - Use the following commands to create and switch to a new branch while in the "main" branch:
    ```sh
    git checkout main  # Ensure you are on the main branch
    git checkout -b <branch_name>
    ```

## Workflow

### Start of Work
- **Pull the latest changes to your local "main" branch:**
  - Example:
    ```sh
    git checkout main
    git pull origin main
    ```

### End of Work (If the task is not completed)
- **Push to the remote feature branch:**
  - Example:
  ```sh
  git push -u origin <branch_name>
  ```

### Finished Work
- **Merge with your local "main" branch and resolve any conflict. When everything works perfectly, push it to remote "main" branch**
  - **Merge with local "main" branch:**
    - Example:
      ```sh
      git checkout main
      git merge <branch_name>
      ```
  - **Resolve any conflicts that arise.**
  - **Push your changes to the remote "main" branch:**
      - Example:
      ```sh
      git push -u origin main
      ```
  - **Delete feature branch (Only if you want to)**
    - Exmaple:
      ```sh
      git checkout main
      git branch -d feature-branch # or git branch -D feature-branch if you need to force delete
      git push origin --delete feature-branch
      ```

### File Required
- **If you require a file that just got updated/pushed to "main" branch, do the following:**
  - **Pull to local "main" branch**
    - Example:
      ```sh
      git checkout main
      git pull origin main 
      ```
  - **Merge the branch with the "main" branch**
    - Example:
      ```sh
      git checkout <branch_name>
      git merge main
      ```

## Neo4j
- **Name the nodes by capitalizing the first character**
  - Example:
    CREATE (**Actor** {...});
- **Name the properties with snake-case and all characters are lower case**
  - Example:
    CREATE (Actor {**name**: "Denzel Washington", **actor_id**: "nm1001213"});