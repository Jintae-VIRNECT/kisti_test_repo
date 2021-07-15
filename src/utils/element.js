/**
 * element의 overflow 여부 체크(스크롤 발생 여부 체크)
 * @param {HTMLElement} element - overflow 여부를 체크할 element
 * @param {HTMLElement} containerElement - (옵션) element의 clientHeight와 scrollHeight가 같아 비교할 수 없는 상황일때 비교대상으로 사용할 container(parent) element
 * @returns
 */
export const isOverflowY = (element, containerElement) => {
  if (containerElement)
    return containerElement.clientHeight < element.clientHeight
  else return element.scrollHeight > element.clientHeight
}
