<template>
  <service-layout></service-layout>
</template>

<script>
import ServiceLayout from 'components/service/ServiceLayout'
// import Axios from 'axios'

// const axios = Axios.create({
// 	timeout: 10000,
// 	withCredentials: true,
//     headers: {
//       'Access-Control-Allow-Origin': 'https://virnectremote.com',
//       'Content-Type': 'application/json',
//       'client': 'web'
//     }
// });

export default {
  name: 'container',
  components: {
    ServiceLayout,
  },

  /* Lifecycles */
  created() {
    const html = document.querySelector('html')
    const windowWidth = window.innerWidth
    let type = 'desktop'

    if (windowWidth <= 1024) {
      type = 'tablet'
    } else if (windowWidth < 768) {
      type = 'mobile'
    }
    html.setAttribute('data-screen', type)

    // window.onerror = (errorMsg, url, lineNumber) => {
    //   let message = 'Error: ' + errorMsg + ' Script: ' + url + ' Line: ' + lineNumber;
    //   // console.trace()
    //   // console.log(message)
    //   axios.post('/logs', { data: message, type: 'ERROR' })
    //     .then(res => {
    //       // console.log(res.data)
    //     })
    // }
  },
}
</script>
