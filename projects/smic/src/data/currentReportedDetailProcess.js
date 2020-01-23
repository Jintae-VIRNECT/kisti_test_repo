import dayjs from 'dayjs'
export default [
  {
    id: '1',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),
    endAt: dayjs()
      .add(1, 'hour')
      .add(23, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'complete',
    auth: 'Jason',
    auths: ['김펭수', '이펭수', '박펭수', '문펭수'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    workerNum: 6,
    issue: true,
  },
  {
    id: '2',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(4, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'complete',
    auth: 'Jason',
    auths: ['김펭수', '이펭수', '박펭수'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '3',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(43, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'idle',
    auth: 'Jason',
    auths: ['김펭수', '이펭수'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '4',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(13, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'imcomplete',
    auths: ['김펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '5',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(33, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'progress',
    auths: ['제갈펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: false,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '6',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(17, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'progress',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: false,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '7',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(7, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: false,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '8',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(38, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '9',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(8, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '10',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(1, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '11',
    processName: "process' Name",
    detailProcessName: "detailProcess' Name",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(50, 'minute'),
    reportedAt: '2020-02-22 19:00',
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
]
