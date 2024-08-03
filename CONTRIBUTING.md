# Contributing to Project

**Tutorial (Devin Lo):** https://docs.google.com/document/d/12TmFKePVelHxftiEZ_8il80g4cj-IeXMhKWBEY1GBWU
## Branching
- **Do not commit directly to the "main" branch. Only merge when the code works**
  - The "main" branch should always be in a deployable state and contain tested production-ready code.

- **Create a new branch for each feature or bug fix.**

- **Only branch off the "main" branch, unless there is a specific reason.**
  - This helps keep the workflow simple and ensures everyone works from the same base code.
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
- **Merge with your local "main" branch and resolve conflicts. When everything works perfectly, push it to the remote "main" branch**
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
    - Example:
      ```sh
      git checkout main
      git branch -d feature-branch # or git branch -D feature-branch if you need to force delete
      git push origin --delete feature-branch
      ```

### File Required
- **If you require a file that just got updated/pushed to the "main" branch, do the following:**
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

## Documentation
- **Document the following:**
  - Class
  - Methods
  - Line of codes (Only if needed)

## Neo4j
- **Name the label by capitalizing the first character and using camel case**
  - Example:
    CREATE (**Actor** {...});
- **Name the properties with camel case**
  - Example:
    CREATE (Actor {**name**: "Denzel Washington", **actorId**: "nm1001213"});
- **Name the relationship with snake case and all characters should be in upper case**
  - Example:
    MATCH (m:Movie) WITH m MATCH (i:Info) WHERE m.movieId = "nm7001542" AND i.infoId = "ml12345678" CREATE (m)-[h:**HAS**]->(i);
- **Add (instance) variables for the labels, properties and relationships for future-proof when modifying the name**