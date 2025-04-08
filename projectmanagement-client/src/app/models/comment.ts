export interface TaskComment {
  id?: number;
  taskId: number;
  authorId: number;
  content: string;
  createdAt?: string;
  updatedAt?: string;
}
