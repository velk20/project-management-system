import {Task} from "./task";
import {User} from "./user";


export interface Project {
  id: number;
  name: string;
  description: string;
  ownerId: number;
  createdAt?: string;
  updatedAt?: string;
  tasks?: Task[];
  teamMembers?: User[];
}

export interface UpdateProject {
  name: string;
  description?: string;
  tasksId?: number[];
  teamMembersId?: number[];
}
