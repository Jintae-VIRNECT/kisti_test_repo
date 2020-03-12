export const processStatus = [
  { label: '대기', color: 'gray', name: 'WAIT', value: 'WAIT' },
  {
    label: '미진행',
    color: 'silver',
    name: 'UNPROGRESSING',
    value: 'UNPROGRESSING',
  },
  { label: '진행', color: 'blue', name: 'PROGRESSING', value: 'PROGRESSING' },
  { label: '미흡', color: 'orange', name: 'INCOMPLETED', value: 'INCOMPLETED' },
  { label: '완료', color: 'green', name: 'COMPLETED', value: 'COMPLETED' },
  { label: '미완수', color: 'dark-gray', name: 'FAILED', value: 'FAILED' },
  { label: '완수', color: 'dark-blue', name: 'SUCCESS', value: 'SUCCESS' },
  { label: '결함', color: 'dark-red', name: 'FAULT', value: 'FAULT' },
]

export const cols = [
  {
    prop: 'name',
    label: '공정 이름',
  },
  {
    prop: 'subProcessTotal',
    label: '세부공정 수',
    width: 80,
  },
  {
    prop: 'schedule',
    label: '공정 일정',
    width: 260,
  },
  {
    prop: 'processPercent',
    label: '진행률',
    width: 150,
  },
  {
    prop: 'doneCount',
    label: '완료된 세부공정',
    width: 100,
  },
  {
    prop: 'conditions',
    label: '진행 상태',
    width: 80,
  },
  {
    prop: 'subProcessAssign',
    label: '세부공정 담당자',
    width: 170,
  },
  {
    prop: 'issueTotal',
    label: '작업 이슈',
    width: 80,
  },
]
