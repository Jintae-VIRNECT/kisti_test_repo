<template>
  <div class="license-card" :class="getClass">
    <div class="license-card--badge" :class="getClass">
      <p>{{ badgeName }}</p>
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

    <ol class="license-card__sub-header">
      <li v-for="(item, idx) in condition" :key="idx">
        <p class="license-card__sub-header--name">{{ item.name }}</p>
        <p class="license-card__sub-header--price">
          {{ item.price }}
        </p>
      </li>
    </ol>

    <ol class="license-card__info">
      <li v-for="(item, idx) in items" :key="idx">
        <p class="license-card__info--name">{{ item.name }}</p>
        <p class="license-card__info--property" :class="item.style">
          {{ item.property }}
        </p>
      </li>
    </ol>
  </div>
</template>

<script>
export default {
  props: {
    level: {
      type: String,
      default: '',
    },
    badgeName: {
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
        basic: this.level === 'BASIC',
        business_term: this.level === 'BUSINESS_TERM​',
        business_permanent: this.level === 'BUSINESS_PERMANENT',
      }
    },
  },
}
</script>

<style></style>
