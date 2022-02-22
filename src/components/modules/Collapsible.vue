<template>
  <div class="collapsible" :class="{ opend: open, decorated: decorated }">
    <button
      type="button"
      class="collapsible__button"
      :class="{ opend: open, decorated: decorated }"
      @click="toggle"
    >
      <span
        class="collapsible__button--title"
        :class="{ decorated: decorated }"
        >{{ title }}</span
      >
      <span
        class="collapsible__button--count"
        :class="{ decorated: decorated }"
        >{{ count }}</span
      >
    </button>

    <transition name="fadeHeight">
      <div v-show="open" class="collapsible__content" :class="{ opend: open }">
        <slot></slot>
      </div>
    </transition>

    <hr
      class="collapsible--divider"
      :class="{ opend: open }"
      v-if="!decorated"
    />
  </div>
</template>

<script>
export default {
  name: 'Collapsible',
  props: {
    title: {
      type: String,
      default: '',
    },
    count: {
      type: Number,
      default: 0,
    },
    showDivider: {
      type: Boolean,
      default: false,
    },
    decorated: {
      type: Boolean,
      default: false,
    },
    preOpen: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      open: false,
    }
  },
  watch: {
    count(cur) {
      if (cur <= 0) {
        this.open = false
      }
    },
  },
  methods: {
    toggle() {
      if (this.count > 0) {
        this.open = !this.open
      }
    },
  },
  mounted() {
    if (this.count > 0) {
      this.open = this.preOpen
    }
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';

.collapsible {
  position: relative;

  &.decorated {
    background: rgb(26, 26, 27);
    border-radius: 3px;
    padding: 1.2143rem 1.2143rem 1.2143rem 1.4286rem;
  }
}
.collapsible__button {
  position: relative;
  background-color: transparent;
  cursor: pointer;
  padding: 1.2857rem 0;
  width: 100%;
  border: none;
  text-align: left;
  outline: none;
  letter-spacing: 0px;

  &::after {
    position: absolute;
    top: 25%;
    right: 0;
    width: 2.4286rem;
    height: 2.4286rem;
    background: url(~assets/image/ic_collapsible_off.svg) 50% no-repeat;
    transform: rotate(0deg);
    content: '';
  }

  &.opend::after {
    transform: rotate(180deg);
  }

  &:hover::after {
    background: url(~assets/image/ic_collapsible_off.svg) #2f2f33 50% no-repeat;
    border-radius: 5px;
    transition: all 0.3s;
  }

  &.decorated {
    padding: 0;

    &::after {
      top: 0px;
      right: 0;
      width: 1.7143rem;
      height: 1.7143rem;
    }
  }
}

.collapsible__button--title {
  font-size: 1.1429rem;
  font-weight: 500;
  letter-spacing: 0px;
  color: rgb(255, 255, 255);
  font-size: 1.1429rem;
  padding-right: 0.5714rem;

  &.decorated {
    font-size: 1rem;
  }
}

.collapsible__button--count {
  color: #8a8a8a;
  font-size: 1.1429rem;
  font-weight: 500;
  letter-spacing: 0px;

  &.decorated {
    font-size: 1rem;
  }
}

.collapsible__content {
  overflow: hidden;
}

.collapsible--divider {
  opacity: 8%;
  border-width: 0px;
  margin: 0;
  border-bottom: 0.5px solid rgb(255, 255, 255);
}

.fadeHeight-enter-active,
.fadeHeight-leave-active {
  transition: all 0.3s;
  max-height: 73.1429rem;
}
.fadeHeight-enter,
.fadeHeight-leave-to {
  opacity: 0;
  max-height: 0px;
}
@include responsive-mobile {
  .collapsible {
    position: relative;
  }
  .collapsible__button--title {
    @include fontLevel(150);
    color: #f7f8f9;
    padding-left: 3.5rem;
  }
  .collapsible__button--count {
    @include fontLevel(150);
    color: #7d8899;
  }

  .collapsible__button {
    &::after {
      position: absolute;
      top: 25%;
      left: 0.4286rem;
      width: 2.4286rem;
      height: 2.4286rem;
      background: url(~assets/image/ic_collapsible_off.svg) 50% no-repeat;
      transform: rotate(0deg);
      content: '';
    }

    &.opend::after {
      transform: rotate(180deg);
    }

    &:hover::after {
      background: url(~assets/image/ic_collapsible_off.svg) 50% no-repeat;
      border-radius: 5px;
      transition: all 0.3s;
    }
  }
}
</style>
