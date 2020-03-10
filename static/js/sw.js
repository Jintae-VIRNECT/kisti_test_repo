/*
 *
 *  Push Notifications codelab
 *  Copyright 2015 Google Inc. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

// console.log('Hello from service-worker.js')

// if ('serviceWorker' in navigator && 'PushManager' in window) {
//   console.log('Service Worker and Push is supported')

//   console.log(navigator)
//   console.log(navigator.serviceWorker)
//   console.log(navigator.ServiceWorkerContainer)

//   navigator.serviceWorker
//     .register('sw.js')
//     .then(swReg => {
//       console.log('Service Worker is registered', swReg)

//       // swRegistration = swReg
//       // initialiseUI()
//     })
//     .catch(error => {
//       console.error(error)
//     })
// } else {
//   console.warn('Push messaging is not supported')
//   // pushButton.textContent = 'Push Not Supported';
// }

self.addEventListener('push', event => {
  console.log('[Service Worker] Push Received.')
  console.log(`[Service Worker] Push had this data: "${event.data.text()}"`)

  const title = '그룹 초대'
  const options = {
    body: event.data.text(),
    icon: '../img/icons/icon.png',
    badge: '../img/icons/badge.png',
  }

  const notificationPromise = self.registration.showNotification(title, options)
  event.waitUntil(notificationPromise)
})

self.addEventListener('notificationclick', event => {
  console.log('[Service Worker] Notification click Received.')

  event.notification.close()

  event.waitUntil(clients.openWindow('https://virnectremote.com'))
})
