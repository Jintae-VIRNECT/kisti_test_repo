/**
 * 에러코드에 대한 정의
 */

export const ERROR = {
  //이미 삭제된 협업에 대한 정의
  REMOTE_ALREADY_REMOVED: 4002,

  //리더가 방을 나가기 했을때 처리
  CONFIRM_REMOTE_LEADER_LEAVE: 4015,

  //동일 계정으로 협업 진행중임
  REMOTE_ALREADY_INVITE: 4016,

  //라이센스가 없는 경우
  NO_LICENSE: 5003,

  //빈 파일을 업로드 시도시
  FILE_DUMMY_ASSUMED: 7002,

  //유효하지 않은 확장자를 가진 파일을 업로드 시도시
  FILE_EXTENSION_UNSUPPORT: 7003,

  //파일 사이즈를 초과한 파일을 업로드 시도시
  FILE_SIZE_EXCEEDED: 7004,

  //스토리지 용량이 가득 찼음.
  FILE_STORAGE_CAPACITY_FULL: 7017,
}
