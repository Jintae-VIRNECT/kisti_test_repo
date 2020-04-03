/**
 * success or fail
 */
export default function randomPromise() {
  return new Promise((resolve, reject) => {
    const bool = Math.round(Math.random())
    if (bool) resolve({ message: 'success' })
    else reject('fail')
  })
}
