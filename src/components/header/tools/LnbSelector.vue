<template>
  <popover
    trigger="click"
    placement="bottom"
    :width="196"
    popperClass="header-lnb-selector"
  >
    <ul>
      <li
        class="header-lnb-selector__button"
        v-for="option of options"
        :key="option.id"
      >
        <button
          @click="changeSelect(option)"
          :class="{ active: option.id === selected.id }"
        >
          <span
            class="header-lnb-selector__check"
            :class="{ active: option.id === selected.id }"
          ></span>
          <p class="header-lnb-selector__title">{{ option.title }}</p>
          <p class="header-lnb-selector__description">
            워크스테이션 멤버: {{ option.member }}명
          </p>
        </button>
      </li>
    </ul>

    <button slot="reference" class="header-workspace-selector">
      {{ selected.title }}
    </button>
  </popover>
</template>

<script>
import Popover from 'Popover'
export default {
  name: 'LnbSelector',
  components: {
    Popover,
  },
  data() {
    return {
      selected: {},
      options: [
        {
          title: '리모트원격솔루션',
          member: 6,
          id: '1',
        },
        {
          title: '버넥트신규사업부',
          member: 99,
          id: '2',
        },
        {
          title: '버넥트신사옥사업부',
          member: 999,
          id: '3',
        },
      ],
    }
  },
  watch: {},
  methods: {
    changeSelect(option) {
      this.selected = option
    },
  },

  /* Lifecycles */
  mounted() {
    this.selected = this.options[0]
  },
}
</script>
