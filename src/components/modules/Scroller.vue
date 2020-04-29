<template>
  <vue2-scrollbar
    ref="contentScrollbar"
    :classes="classes"
    @onScroll="onScroll"
    :onMaxScroll="onMaxScroll"
    :style="scrollerStyle"
  >
    <div class="scroll--inner">
      <slot></slot>
    </div>
  </vue2-scrollbar>
</template>

<script>
function calcOffset(element) {
  let top = 0
  let left = 0

  do {
    top += element.offsetTop || 0
    left += element.offsetLeft || 0
    element = element.offsetParent
  } while (element)

  return { top, left }
}

export default {
  name: 'Scroller',
  props: {
    className: {
      type: String,
    },
    onMaxScroll: Function,
    stopPropagation: {
      type: Boolean,
      default: () => false,
    },
    height: {
      type: [Number, String],
      deafult: 0,
    },
  },
  data() {
    return {
      scrollH: 0,
      isScrolled: false,
      eventThrottle: false,
    }
  },
  computed: {
    classes() {
      let classname = ''

      if (this.isScrolled) {
        classname += 'shadow '
      }
      if (this.className) {
        classname += this.className
      }

      return classname
    },
    scrollerStyle() {
      if (typeof this.scrollH === 'string') {
        return {
          height: this.scrollH,
        }
      } else {
        return {
          height: `${this.scrollH}px`,
        }
      }
    },
  },
  watch: {
    $route: function() {
      if (this.$refs['contentScrollbar']) {
        this.$refs['contentScrollbar'].reset()
      }
    },
  },
  methods: {
    onScroll(scrollX, scrollY) {
      this.isScrolled = scrollY > 30

      //이벤트 발생 스로틀 처리.
      this.eventThrottle && clearTimeout(this.eventThrottle)
      if (!this.stopPropagation) {
        this.eventThrottle = setTimeout(() => {
          document.dispatchEvent(new Event('click'))
        }, 100)
      }
    },
    getScrollH(target) {
      if (typeof this.height === 'number' && this.height > 0) {
        this.scrollH = this.height + 'px'
        return this.height
      }
      if (typeof this.height === 'string') {
        this.scrollH = this.height
        return this.height
      }
      let element
      let totalHeight = document.body.offsetHeight

      if ('offsetTop' in target) {
        element = target
      } else if ('$el' in target) {
        element = target.$el
      } else {
        return totalHeight
      }
      this.scrollH = element.parentElement.offsetHeight
      return element.parentElement.offsetHeight

      // this.scrollH = totalHeight - calcOffset(element).top
      // return totalHeight - calcOffset(element).top
    },
    reset() {
      if (this.$refs['contentScrollbar']) {
        this.$refs['contentScrollbar'].reset()
        this.isScrolled = false
      }
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$off('scrollReset')
    this.$nextTick(() => {
      this.$eventBus.$on('scrollReset', this.reset)
    })
  },
  mounted() {
    this.getScrollH(this.$refs['contentScrollbar'])
  },
  updated() {
    this.getScrollH(this.$refs['contentScrollbar'])
  },
}
</script>
