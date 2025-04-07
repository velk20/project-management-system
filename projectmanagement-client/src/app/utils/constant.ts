export class Constant {
  static BASE_URL = 'http://localhost:8080/api/v1';
  static AUTH_URL = `${Constant.BASE_URL}/auth`;
  static PROJECTS_URL = `${Constant.BASE_URL}/projects`;
  static USERS_URL = `${Constant.BASE_URL}/users`;
  static TASKS_URL = `${Constant.BASE_URL}/tasks`;
  static COMMENTS_URL = `${Constant.TASKS_URL}/{taskId}/comments`;
}
