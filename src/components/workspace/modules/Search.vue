<template>
  <div class="search" :class="appendClass">
    <input
      class="search__input"
      type="text"
      :placeholder="placeholder"
      v-on:input="search = $event.target.value"
    />
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import * as Regexp from 'utils/regexp'
export default {
  name: 'Search',
  props: {
    placeholder: {
      type: String,
      default: '',
    },
    appendClass: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      search: '',
    }
  },
  watch: {
    search(sch) {
      sch = Regexp.escapeRegExp(sch)
      this.$emit('search', sch)
      this.setFilter(sch)
    },
  },
  methods: {
    ...mapActions(['setFilter']),
  },

  /* Lifecycles */
  mounted() {
    this.setFilter(this.search)
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.search {
  position: relative;
  width: fit-content;
  height: 2.571rem;
  padding: 0 2.571rem 0 1.143rem;
  background-color: transparent;
  border: solid 1px rgba(#b3b3b3, 0.2);
  border-radius: 1.286rem;
  &:before {
    position: absolute;
    top: 0.429rem;
    right: 0.714rem;
    width: 1.571rem;
    height: 1.571rem;
    background: url(~assets/image/ic_search.svg) 50%/1.571rem no-repeat;
    content: '';
  }
  &:focus-within {
    border: solid 1px $color_primary;
  }
}
.search__input {
  width: 13.286rem;
  height: 100%;
  color: #fff;
  font-size: 1rem;
  background-color: transparent;
  border: none;
  transition: width 0.25s ease;
  caret-color: $color_primary;
  &:focus {
    width: 22.857rem;
    transition: width 0.25s ease;
  }
  &::placeholder {
    color: rgba(#fff, 0.4);
  }
}
</style>
