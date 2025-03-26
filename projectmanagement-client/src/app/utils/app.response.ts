export interface AppResponseWithMessage extends AppResponse, WithMessage {

}

export interface AppResponseWithNoData extends AppResponseCodeAndStatus, WithMessage{

}

export interface AppResponse extends AppResponseCodeAndStatus{
  data: object | object[]
}

export interface AppResponseCodeAndStatus  {
  code: number;
  status: string;
}

export interface WithMessage  {
  message: string;
}
