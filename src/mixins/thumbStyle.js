export default {
  data() {
    return {
      defaultWidth: '',
      defaultHeight: '',
      mobileWidth: '',
      mobileHeight: '',
      mobileSizeEventFn: null,
    }
  },
  methods: {
    //thumbStyle 값 세팅
    setSizeVariable(defaultWidth, defaultHeight, mobileWidth, mobileHeight) {
      this.defaultWidth = defaultWidth
      this.defaultHeight = defaultHeight
      this.mobileWidth = mobileWidth
      this.mobileHeight = mobileHeight
    },
    setThumbStyle(width, height) {
      this.thumbStyle = { width, height }
    },
    //모바일 스크린 사이즈에서 실행
    mobileSizeEventFunction() {
      this.setThumbStyle(this.mobileWidth, this.mobileHeight)
    },
    //모바일 이외 스크린 사이즈에서 실행
    resetSizeEventFunction() {
      this.setThumbStyle(this.defaultWidth, this.defaultHeight)
    },
    //모바일 사이즈 이벤트 리스너 활성화
    activateThumbStyleHandlerOnMobileSize() {
      //mobile screen size event 함수 생성
      this.mobileSizeEventFn = this.callAndGetMobileResponsiveFunction(
        this.mobileSizeEventFunction,
        this.resetSizeEventFunction,
      )

      //이벤트 리스너 활성화
      this.addEventListenerScreenResize(this.mobileSizeEventFn)
    },
  },
  beforeDestroy() {
    //이벤트 함수 제거
    if (this.mobileSizeEventFn)
      this.removeEventListenerScreenResize(this.mobileSizeEventFn)
  },
}
