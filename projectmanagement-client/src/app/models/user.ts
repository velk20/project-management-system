export interface User {
  id?: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface ChangeUserPassword {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface UserDetails{
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  active: boolean;
}
