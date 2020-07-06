<template>
  <div class="license-card">
    <div class="license-card--badge" :class="getClass">
      <p>{{ planName }}</p>
    </div>
    <div class="license-card__header">
      <p class="license-card__header--count">{{ perCount }}</p>
      <p
        class="license-card__header--price"
        :class="{
          nocount: perCount.length === 0,
        }"
      >
        {{ perPerice }}
      </p>
    </div>

    <table class="license-card__sub-header">
      <tr></tr>
      <tr v-for="(item, idx) in condition" :key="idx">
        <td class="license-card__sub-header--name">{{ item.name }}</td>
        <td class="license-card__sub-header--property">{{ item.price }}</td>
      </tr>
    </table>

    <div class="license-card__info">
      <p class="license-card__info--title">포함된 기능</p>
      <div class="license-card__info--underbar"></div>
      <table class="license-card__info-table">
        <tr v-for="(item, idx) in items" :key="idx">
          <td class="license-card__info-table--name">{{ item.name }}</td>
          <td class="license-card__info-table--property" :class="item.style">
            {{ item.property }}
          </td>
        </tr>
      </table>
    </div>

    <button @click="buy" class="license-card--buy btn">구매 이동</button>
  </div>
</template>

<script>
export default {
  props: {
    plan: {
      type: String,
      default: '',
    },
    planName: {
      type: String,
      default: '',
    },
    perCount: {
      type: String,
      default: '',
    },
    perPerice: {
      type: String,
      default: '',
    },
    condition: {
      type: Array,
      default: () => [],
    },
    items: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    getClass() {
      return {
        basic: this.plan === 'BASIC',
        business_term: this.plan === 'BUSINESS_TERM​',
        business_permanent: this.plan === 'BUSINESS_PERMANENT',
      }
    },
  },
  methods: {
    buy() {
      console.log('라이센스 구입')
    },
  },
}
</script>

<style></style>
