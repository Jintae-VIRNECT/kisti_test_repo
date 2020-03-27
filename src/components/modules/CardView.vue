<template>
  <div class="cardview">
    <card v-for="card of list">
      <slot></slot>
    </card>
  </div>
</template>

<script>
import Card from 'Card'
export default {
  name: 'CardView',
  components: {
    Card,
  },
  props: {},
  data() {
    return {
      list: [
        {
          profile: require('assets/image/img-default-user.svg'),
          name: '버넥트 리모트 01',
          id: 'example@example.com',
          role: 'Master',
        },
        {
          profile: require('assets/image/img-default-user.svg'),
          name: '버넥트 리모트 01',
          id: 'example@example.com',
          role: 'Master',
        },
        {
          profile: require('assets/image/img-default-user.svg'),
          name: '버넥트 리모트 01',
          id: 'example@example.com',
          role: 'Master',
        },
      ],
    }
  },
  computed: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
.cardview {
}
</style>
