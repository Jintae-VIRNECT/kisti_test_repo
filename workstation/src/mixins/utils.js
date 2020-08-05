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
  },
}
