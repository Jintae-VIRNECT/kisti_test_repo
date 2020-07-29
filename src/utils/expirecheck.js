export const expireCheck = time => {
  const diff = new Date(time).getTime() - Date.now()
  console.log('expire', diff > 0)
  return diff > 0
}
