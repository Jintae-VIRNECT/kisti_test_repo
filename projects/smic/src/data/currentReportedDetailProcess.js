import dayjs from 'dayjs'
export default [
  {
    id: '1',
    processName: '전기오토바이 제조',
    detailProcessName: '자제 절단',
    startAt: dayjs().minute(0),
    endAt: dayjs()
      .add(1, 'hour')
      .minute(0),
    reportedAt: dayjs().subtract(1, 'month'),
    status: 'progress',
    auth: 'Jason',
    auths: ['전문가 1', '전문가 2', '전문가 3'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 3,
    processPercent: 70,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    done: false,
    workerNum: 6,
    workName: '절단기 준비',
    jobId: 'Job ID 1',
    issue: true,
  },
  {
    id: '2',
    processName: '터빈 조립',
    detailProcessName: "detailProcess' Name 2",
    startAt: dayjs().minute(0),
    endAt: dayjs()
      .add(2, 'hour')
      .minute(0),
    reportedAt: dayjs().subtract(2, 'month'),
    status: 'complete',
    auth: 'Jason',
    auths: ['전문가 1', '전문가 2', '전문가 3', '전문가 4', '전문가 5'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 5,
    processPercent: 100,
    workerNum: 6,
    workName: '전원 연결',
    jobId: 'Job ID 2',
    issue: false,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    done: false,
  },
  {
    id: '3',
    processName: '모터 펌프 조립',
    detailProcessName: "detailProcess' Name 3",
    startAt: dayjs().minute(0),
    endAt: dayjs()
      .add(3, 'hour')
      .minute(0),
    reportedAt: dayjs().subtract(3, 'month'),
    status: 'idle',
    auth: 'Jason',
    auths: ['전문가 4', '전문가 5', '전문가 6', '전문가 7'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 4,
    processPercent: 90,
    workerNum: 6,
    workName: '터빈 모터 전력 연결',
    jobId: 'Job ID 3',
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    done: false,
  },
  {
    id: '4',
    processName: '배기관 조립',
    detailProcessName: "detailProcess' Name 4",
    startAt: dayjs().minute(0),
    endAt: dayjs()
      .add(4, 'hour')
      .minute(0),
    reportedAt: dayjs().subtract(4, 'month'),
    status: 'imcomplete',
    auths: ['전문가 3', '전문가 4', '전문가 5'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 3,
    processPercent: 90,
    workerNum: 6,
    workName: '모터 펌프 구동 점검',
    jobId: 'Job ID 4',
    issue: true,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    done: false,
  },
  {
    id: '5',
    processName: '계기판 조립',
    detailProcessName: "detailProcess' Name 5",
    startAt: dayjs().minute(0),
    endAt: dayjs()
      .add(5, 'hour')
      .minute(0),
    reportedAt: dayjs().subtract(5, 'month'),
    status: 'progress',
    auths: ['전문가 4', '전문가 5', '전문가 6', '전문가 7', '전문가 8'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 5,
    processPercent: 90,
    workerNum: 6,
    workName: '외부 배기구 정상 작동 점검',
    jobId: 'Job ID 5',
    issue: false,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    done: false,
  },
  {
    id: '6',
    processName: '전기 배선 연결',
    detailProcessName: "detailProcess' Name 6",
    startAt: dayjs().minute(0),
    endAt: dayjs()
      .add(6, 'hour')
      .minute(0),
    reportedAt: dayjs().subtract(6, 'month'),
    status: 'progress',
    auths: ['전문가 6', '전문가 7', '전문가 8', '전문가 9', '전문가 10'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 4,
    processPercent: 90,
    workerNum: 6,
    workName: '계기판 전력량 반영 확인',
    jobId: 'Job ID 6',
    issue: false,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    done: false,
  },
  {
    id: '7',
    processName: '게이지 조립',
    detailProcessName: '자제 절단',
    startAt: dayjs().minute(0),
    endAt: dayjs()
      .add(7, 'hour')
      .minute(0),
    reportedAt: dayjs().subtract(7, 'month'),
    status: 'imcomplete',
    auths: ['전문가 3', '전문가 4', '전문가 5'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 3,
    processPercent: 90,
    workerNum: 6,
    workName: '전기 배선 안전 점검',
    jobId: 'Job ID 7',
    issue: false,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    done: false,
  },
]
