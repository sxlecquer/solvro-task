
# 🛠️ RESTful Project Management Tool

**REST API** built on **Spring Boot** that allows project managers to maintain projects, assign project tasks to developers, and track project progress, leveraging a **PostgreSQL** database for persistent storage.<br>

The application implements a sophisticated **task assignment algorithm** that selects developers based on their specialization and estimated time of completed tasks.<br>

Comprehensive **testing coverage** for all layers of the application and detailed **exception handling** for incorrect requests (e.g. non-existent developer emails, specialization mismatch) are also integrated.

## Application endpoints

#### Register developer

```java
POST /developer
```
Accepts basic information about the developer, such as email and specialization.

| Payload             | Required  | Description                 |
| :-----------------  | :-------  | :-------------------------  |
| `email`             | `true`    | Email address of developer  |
| `specialization`    | `true`    | Developer's specialization  |

#

#### Create project

```java
POST /project
```
Accepts basic project information and a list of developers to assign. If a developer does not exist, one will be created.

| Payload           | Required  | Description                                                  |
| :---------------  | :-------  | :----------------------------------------------------------  |
| `name`            | `true`    | Project name                                                 |
| `description`     | `true`    | Project description                                          |
| `developerEmails` | `true`    | List of emails of developers to be assigned to this project  |

# 

#### Display project

```java
GET /project/{id}
```
Accepts the project id as a *path variable* and returns complete information about the found project.

# 

#### Display developer projects

```java
GET /project?email={developer email}
```
Accepts a developer's email as a *request parameter* and returns complete information about their projects, if any.

# 

#### Add task

```java
POST /project/{id}/task
```
Allows to add a task for a project.

| Payload             | Required  | Description                                                |
| :-----------------  | :-------  | :--------------------------------------------------------  |
| `name`              | `true`    | Task name                                                  |
| `estimation`        | `true`    | Estimated completion time (number from Fibonacci sequence) |
| `specialization`    | `true`    | Task specialization                                        |
| `assignedDeveloper` | `false`   | Email of the assigned developer for this task              |

# 

#### Edit task

```java
PATCH /project/{projectId}/task/{taskId}
```
Accepts the project and task ids as a *path variables* and updated task status. Returns complete information about the edited task.

| Payload             | Required  | Description                                      |
| :-----------------  | :-------  | :----------------------------------------------  |
| `state`             | `true`    | Task completion status                           |
| `assignedDeveloper` | `false`   | Email of newly assigned developer for this task  |

# 

#### Suggest task assignment

```java
POST project/{projectId}/task/assignment
```
Accepts the project id as a *path variable* and returns a task assignment suggestion for all unassigned tasks.<br>
The assignment algorithm selects developers based on their specialization and estimated time of completed tasks.

# 

#### Accept task assignment

```java
PUT project/{projectId}/task/assignment/{assignId}?accepted={true/false}
```
Accepts the project id and task assignment suggestion id as a *path variables*, and whether to accept this task assignment suggestion as a *request parameter*.<br>
Returns information about an accepted task or a rejection message otherwise.

#
