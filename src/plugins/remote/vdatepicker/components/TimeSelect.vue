<template>
  <div class="vc-select">
    <select v-bind="$attrs" @change="$emit('input', $event.target.value)">
      <option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
        :disabled="option.disabled"
      >
        {{ option.label }}
      </option>
    </select>
    <div class="vc-select-arrow">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
        <path
          d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"
        />
      </svg>
    </div>
  </div>
</template>

<script>
export default {
  inheritAttrs: false,
  props: {
    options: Array,
  },
}
</script>

<style lang="scss" scoped>
.vc-select {
  position: relative;
  & select {
    display: block;
    flex-grow: 1;
    width: 52px;
    height: 30px;
    padding: 0 20px 0 8px;
    color: var(--gray-900);
    font-weight: var(--font-medium);
    font-size: var(--text-base);
    line-height: var(--leading-tight);
    text-align: left;
    text-indent: 0px;
    background-color: var(--gray-200);
    border: 2px solid;
    border-color: var(--gray-200);
    border-radius: var(--rounded);
    cursor: pointer;
    appearance: none;
    -moz-padding-start: 3px;
    &:hover {
      color: var(--gray-600);
    }
    &:focus {
      background-color: var(--white);
      border-color: var(--accent-400);
      outline: 0;
    }
  }
}
.vc-select-arrow {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  padding: 0 4px 0 0;
  color: var(--gray-500);
  pointer-events: none;
  & svg {
    width: 16px;
    height: 16px;
    fill: currentColor;
  }
}
.vc-is-dark {
  & select {
    color: var(--gray-100);
    background: var(--gray-700);
    border-color: var(--gray-700);
    &:hover {
      color: var(--gray-400);
    }
    &:focus {
      background-color: var(--gray-800);
      border-color: var(--accent-500);
    }
  }
}
</style>
