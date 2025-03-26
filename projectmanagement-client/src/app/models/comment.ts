export interface TaskComment {
  taskId: number;
  authorId: number;
  content: string;
  createdAt?: string;
  updatedAt?: string;
}
