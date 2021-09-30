<template>
  <div class="project__popupItem" :style="cssVars">
    <span class="color"></span>
    <div class="used">
      <span class="count"> {{ title }} </span>
    </div>
    <div class="remain">
      <span> {{ byte }} GB</span>
    </div>
  </div>
</template>

<script>
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  props: {
    title: String,
    byte: Number,
    color: String,
  },
  computed: {
    cssVars() {
      return {
        '--color': this.color,
      }
    },
  },
}
</script>

<style lang="scss">
.project__popupItem {
  & > span.color {
    display: inline-block;
    width: 12px;
    height: 12px;
    margin-right: 8px;
    border-radius: 2px;
    vertical-align: middle;
    background: var(--color);
    content: '';
  }
  .used {
    display: inline-block;
    font-size: 14px;
    .count {
      color: $font-color-content;
    }
  }
  .remain {
    float: right;
    font-size: 13px;
  }
}
</style>
