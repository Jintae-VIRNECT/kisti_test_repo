<template>
  <component
    v-bind:is=""
    v-bind="linkProps(link)"
    :target="target"
    :title="title"
    class="ui-anchor"
    :class="classname"
    @mouseover="this.mouseover"
    @mouseenter="this.mouseenter"
    @mouseleave="this.mouseleave"
    @mousedown="this.mousedown"
    @mouseup="this.mouseup"
  >
    <slot></slot>
  </component>
</template>

<script>
import { validLink } from 'utils/regexp'
export default {
  name: 'ui-anchor',
  props: {
    link: {
      type: [String, Object],
      default: '',
    },
    target: String,
    title: String,
    className: String,
  },
  data() {
    return {
      isHover: false,
    }
  },
  computed: {
    classname() {
      const classname = []

      if (this.className) {
        classname.push(this.className)
      }

      if (this.isHover === true) {
        classname.push('ui-anchor__hover')
      }

      return classname.join(' ')
    },
  },
  methods: {
    linkProps(url) {
      if (typeof url === 'string' && validLink(url)) {
        return {
          is: 'a',
          href: url,
          rel: 'noopener',
          ...this.$attrs,
        }
      } else {
        return {
          is: 'router-link',
          to: url,
          ...this.$attrs,
        }
      }
    },
    click(event) {
      if (this.$listeners.hasOwnProperty('click'))
        this.$listeners.click.bind(this)(event)
    },
    mouseover(event) {
      this.isHover = true
      if (this.$listeners.hasOwnProperty('mouseover'))
        this.$listeners.mouseover.bind(this)
    },
    mouseenter(event) {
      if (this.$listeners.hasOwnProperty('mouseenter'))
        this.$listeners.mouseenter.bind(this)
    },
    mouseleave(event) {
      this.isHover = false
      if (this.$listeners.hasOwnProperty('mouseleave'))
        this.$listeners.mouseleave.bind(this)
    },
    mousedown(event) {
      if (this.$listeners.hasOwnProperty('mousedown'))
        this.$listeners.mousedown.bind(this)
    },
    mouseup() {
      if (this.$listeners.hasOwnProperty('')) this.$listeners.mouseup.bind(this)
    },
  },
}
</script>

<style lang="scss" scoped></style>
