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
