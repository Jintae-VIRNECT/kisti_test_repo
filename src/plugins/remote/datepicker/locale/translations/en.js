import Language from '../Language'

const language = new Language(
  'English',
  ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
  ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
  ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
)
language.yearSuffix = '-'
language.ymd = true

export default language
// eslint-disable-next-line
;
