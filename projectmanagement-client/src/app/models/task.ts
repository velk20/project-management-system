import {TaskComment} from "./comment";
import {TaskStatus} from "./task-status.enum";
import {TaskType} from "./task-type.enum";

export interface Task {
  id?: number;
  title: string;
  description?: string;
  status: TaskStatus;
  type: TaskType;
  creatorId: number;
  assigneeId?: number;
  projectId: number;
  createdAt?: string;
  updatedAt?: string;
  comments?: TaskComment[];
}

export interface PageableTasks {
  totalPages: number;
  totalElements: number;
  tasks: Task[];
}
