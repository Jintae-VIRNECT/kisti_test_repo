<template>
  <div class="license-card" :class="getClass">
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
    <div class="license-card--underbar"></div>

    <table class="license-card__sub-header">
      <tr v-for="(item, idx) in condition" :key="idx">
        <td class="license-card__sub-header--name">{{ item.name }}</td>
        <td class="license-card__sub-header--price">{{ item.price }}</td>
      </tr>
    </table>

    <table class="license-card__info">
      <tr v-for="(item, idx) in items" :key="idx">
        <td class="license-card__info--name">{{ item.name }}</td>
        <td class="license-card__info--property" :class="item.style">
          {{ item.property }}
        </td>
      </tr>
    </table>
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
}
</script>

<style></style>
