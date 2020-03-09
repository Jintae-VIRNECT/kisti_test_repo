export const processStatus = [
  { label: '대기', name: 'wait', value: 'wait' },
  { label: '미진행', name: 'idle', value: 'idle' },
  { label: '진행', name: 'progress', value: 'progress' },
  { label: '미흡', name: 'insufficient', value: 'insufficient' },
  { label: '완료', name: 'complete', value: 'complete' },
  { label: '미완수', name: 'incompletion', value: 'incompletion' },
  { label: '완수', name: 'fulfill', value: 'fulfill' },
  { label: '결함', name: 'fault', value: 'fault' },
]

export const cols = [
  {
    prop: 'name',
    label: '공정 이름',
  },
  {
    prop: 'subProcessTotal',
    label: '세부공정 수',
    width: 100,
  },
  {
    prop: 'schedule',
    label: '공정 일정',
  },
  {
    prop: 'processPercent',
    label: '진행률',
    width: 150,
  },
  {
    prop: 'status',
    label: '진행 상태',
    width: 100,
  },
  {
    prop: 'auths',
    label: '세부공정 담당자',
    width: 200,
  },
  {
    prop: 'issue',
    label: '작업 이슈',
    width: 80,
  },
]
