export default {
  methods: {
    print(url) {
      const popup = window.open('', '_blank')
      popup.document.write(`
        <style>
          @media print {  
            @page {
              size: 210mm 297mm;
              margin: 25mm;
              margin-right: 45mm;
            }
          }
          img {
            width: 100mm;
            height: 100mm;
            border: solid;
          }
        </style>
      `)
      popup.document.write(`<img src="${url}" />`)
      popup.document.close()
      setTimeout(() => popup.print(), 1)
    },
  },
}
