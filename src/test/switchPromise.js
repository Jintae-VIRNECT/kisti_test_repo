/**
 * success or fail
 */
let sw = true

export default function switchPromise() {
  return new Promise((resolve, reject) => {
    sw = sw ? false : true
    if (sw) resolve({ message: 'success' })
    else
      reject(
        'long long long long long long long long long long long long long long long fail message',
      )
  })
}
