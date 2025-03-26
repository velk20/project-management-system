import {TaskComment} from "./comment";

export interface Task {
  id?: number;
  title: string;
  description?: string;
  status: string
  type: string
  creatorId: number;
  assigneeId?: number;
  projectId: number;
  createdAt?: string;
  updatedAt?: string;
  comments?: TaskComment[];
}
