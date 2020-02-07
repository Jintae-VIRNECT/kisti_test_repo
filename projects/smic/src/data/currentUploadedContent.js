import dayjs from 'dayjs'
export default [
  {
    id: '1',
    name: '전기오토바이 제조',
    uploadedAt: dayjs('2020-02-10')
      .add(0, 'hour')
      .add(23, 'minute'),
    detailProcess: 3,
    auth: '전문가 1',
    profileImg: require('@/assets/image/profileImg.jpg'),
    profile_img: '',
    volume: '20',
    contentPublish: 'managing',
    processRegister: true,
  },
  {
    id: '2',
    name: '터빈 조립',
    uploadedAt: dayjs('2020-02-10')
      .add(1, 'hour')
      .add(3, 'minute'),
    detailProcess: 5,
    auth: '전문가 2',
    profileImg: require('@/assets/image/profileImg.jpg'),
    profile_img: '',
    volume: '20',
    contentPublish: 'managing',
    processRegister: false,
  },
  {
    id: '3',
    name: '모터 펌프 조립',
    uploadedAt: dayjs('2020-02-09')
      .add(2, 'hour')
      .add(37, 'minute'),
    detailProcess: 4,
    auth: '전문가 3',
    profileImg: require('@/assets/image/profileImg.jpg'),
    profile_img: '',
    volume: '20',
    contentPublish: 'managing',
    processRegister: true,
  },
  {
    id: '4',
    name: '배기관 조립',
    uploadedAt: dayjs('2020-02-08')
      .add(3, 'hour')
      .add(47, 'minute'),
    detailProcess: 3,
    auth: '전문가 4',
    profileImg: require('@/assets/image/profileImg.jpg'),
    profile_img: '',
    volume: '20',
    contentPublish: 'managing',
    processRegister: false,
  },
  {
    id: '5',
    name: '계기판 조립',
    uploadedAt: dayjs('2020-02-10')
      .add(4, 'hour')
      .add(15, 'minute'),
    detailProcess: 5,
    auth: '전문가 5',
    profileImg: require('@/assets/image/profileImg.jpg'),
    profile_img: '',
    volume: '20',
    contentPublish: 'managing',
    processRegister: true,
  },
  {
    id: '6',
    name: '전기 배선 연결',
    uploadedAt: dayjs('2020-02-09')
      .add(5, 'hour')
      .add(26, 'minute'),
    detailProcess: 4,
    auth: '전문가 6',
    profileImg: require('@/assets/image/profileImg.jpg'),
    profile_img: '',
    volume: '20',
    contentPublish: 'managing',
    processRegister: true,
  },
  {
    id: '7',
    name: '게이지 조립',
    uploadedAt: dayjs('2020-02-08')
      .add(6, 'hour')
      .add(3, 'minute'),
    detailProcess: 3,
    auth: '전문가 7',
    profileImg: require('@/assets/image/profileImg.jpg'),
    profile_img: '',
    volume: '20',
    contentPublish: 'managing',
    processRegister: true,
  },
]
