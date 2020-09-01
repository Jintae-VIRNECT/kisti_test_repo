export default {
  methods: {
    print(url, size = 10) {
      const popup = window.open('', '_blank')
      popup.document.write(`
        <style>
          @media print {  
            @page {
              size: 210mm 297mm;
              margin: 5mm;
            }
          }
          img {
            width: ${size}cm;
            height: ${size}cm;
            border: solid;
          }
        </style>
      `)
      popup.document.write(`<img src="${url}" />`)
      popup.document.close()
      setTimeout(() => popup.print(), 1)
    },
    download(url, name) {
      if (this.$config.TARGET_ENV !== 'production') {
        alert('개발환경에서는 파일명 설정과 자동 다운로드가 동작하지 않습니다')
      }
      const a = document.createElement('a')
      a.href = url
      a.download = name
      a.target = '_blank'
      a.dispatchEvent(new MouseEvent('click'))
    },
    measureText(text, font = '15px NotoSansKR') {
      const ctx = document.createElement('canvas').getContext('2d')
      ctx.font = font
      return ctx.measureText(text).width
    },
  },
}
