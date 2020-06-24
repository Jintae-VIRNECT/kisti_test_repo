<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>{{ $t('terms.page.title') }}</h2>
				<p>{{ $t('terms.page.pageInfo') }}</p>

				<div class="terms-body">
					<el-checkbox v-model="allTerms" class="all-terms">{{
						$t('terms.page.entire')
					}}</el-checkbox>
					<el-checkbox v-model="serviceAgree" class="must-check">{{
						$t('terms.page.termsContents')
					}}</el-checkbox>
					<div class="terms-contents terms-wraper">
						<h3>{{ $t('terms.h2') }}</h3>

						<div class="policy-body">
							<ul>
								<li v-for="(list, idx) of $t('terms.lists')" :key="idx">
									<h4>
										{{ list.title }}
									</h4>
									<p v-if="list.contents">{{ list.contents }}</p>
									<ul class="depth">
										<li v-for="(word, i) of list.wordArray" :key="i">
											<p>
												<span>{{ word.word }}</span>
												<i
													v-html="word.wordContents"
													v-if="word.wordContents"
												></i>
											</p>
											<ul class="depth-inner" v-if="word.wordList">
												<li v-for="(hi, val) of word.wordList.array" :key="val">
													<p>{{ hi.text }}</p>
													<ul class="dot-list" v-if="hi.depthArray">
														<li v-for="(l, val) of hi.depthArray" :key="val">
															<p>{{ l }}</p>
														</li>
													</ul>
												</li>
											</ul>
										</li>
									</ul>
								</li>
							</ul>
						</div>
					</div>

					<el-checkbox v-model="privacyAgree" class="must-check">{{
						$t('terms.page.privacyPolicyContents')
					}}</el-checkbox>
					<div class="policy-contents terms-wraper">
						<h3>{{ $t('policy.h2') }}</h3>
						<p>{{ $t('policy.h2Contents') }}</p>
						<div class="policy-head">
							<p>{{ $t('policy.infoList.contents') }}</p>
							<ul>
								<li
									v-for="(list, idx) of $t('policy.infoList.array')"
									:key="idx"
								>
									{{ idx + 1 }}. {{ list }}
								</li>
							</ul>
						</div>

						<div class="policy-body">
							<ul>
								<li v-for="(list, idx) of $t('policy.lists')" :key="idx">
									<h4>
										<span>{{ idx + 1 }}.</span> {{ list.title }}
									</h4>
									<p v-if="list.contents">{{ list.contents }}</p>
									<ul class="depth">
										<li v-for="(word, i) of list.wordArray" :key="i">
											<p>
												<span>{{ word.word }}</span>
												<i
													v-html="word.wordContents"
													v-if="word.wordContents"
												></i>
											</p>
											<ul class="depth-inner" v-if="word.wordList">
												<li v-for="(hi, val) of word.wordList.array" :key="val">
													<p>{{ hi.text }}</p>
													<ul class="dot-list" v-if="hi.depthArray">
														<li v-for="(l, val) of hi.depthArray" :key="val">
															<p>{{ l }}</p>
														</li>
													</ul>
												</li>
											</ul>
										</li>
									</ul>
								</li>
							</ul>
						</div>
					</div>
					<el-checkbox v-model="marketingAgree">{{
						$t('terms.page.marketingReceive')
					}}</el-checkbox>
				</div>

				<el-button
					class="next-btn block-btn"
					type="info"
					@click="
						$router.push({
							name: 'signup',
							params: {
								marketInfoReceive: marketingAgree,
								policyAgree: privacyAgree,
							},
						})
					"
					:disabled="!privacyAgree || !serviceAgree"
					>다음</el-button
				>
			</el-col>
		</el-row>
	</div>
</template>

<script>
export default {
	data() {
		return {
			allTerms: null,
			serviceAgree: false,
			privacyAgree: false,
			marketingAgree: false,
		}
	},
	watch: {
		allTerms() {
			if (
				this.allTerms &&
				!this.serviceAgree &&
				!this.privacyAgree &&
				!this.marketingAgree
			) {
				this.serviceAgree = true
				this.privacyAgree = true
				this.marketingAgree = true
			}
			if (
				!this.allTerms &&
				this.serviceAgree &&
				this.privacyAgree &&
				this.marketingAgree
			) {
				this.serviceAgree = false
				this.privacyAgree = false
				this.marketingAgree = false
			}
			if (
				this.allTerms &&
				(this.serviceAgree || this.privacyAgree || this.marketingAgree)
			) {
				this.serviceAgree = true
				this.privacyAgree = true
				this.marketingAgree = true
			}
			if (
				!this.allTerms ||
				!this.serviceAgree ||
				!this.privacyAgree ||
				!this.marketingAgree
			) {
				this.allTerms = false
			}
		},
		serviceAgree() {
			if (!this.serviceAgree) return (this.allTerms = false)
			if (this.serviceAgree && this.privacyAgree && this.marketingAgree) {
				this.allTerms = true
			}
		},
		privacyAgree() {
			if (!this.privacyAgree) return (this.allTerms = false)
			if (this.serviceAgree && this.privacyAgree && this.marketingAgree) {
				this.allTerms = true
			}
		},
		marketingAgree() {
			if (!this.marketingAgree) return (this.allTerms = false)
			if (this.serviceAgree && this.privacyAgree && this.marketingAgree) {
				this.allTerms = true
			}
		},
	},
	computed: {
		checkAll() {
			if (this.serviceAgree && this.privacyAgree && this.marketingAgree) {
				return true
			}
		},
	},
}
</script>

<style lang="scss" scoped>
p {
	font-family: 'NotoSansKR', 'Noto Sans';
}

.el-button.next-btn {
	margin-top: 60px;
}

.terms-body {
	margin-top: 55px;
	text-align: left;
	.el-checkbox {
		display: block;
		margin-top: 26px;
		margin-bottom: 12px;
	}
}
.terms-wraper {
	height: 132px;
	margin-top: 11px;
	padding: 14px;
	overflow-y: scroll;
	font-size: 13px;
	border: 2px solid #ecf0f5;
}
@import '~assets/css/modules/policy.scss';
</style>
