<template>
    <div class="Attribute">
        <el-tabs v-model="activeLabelName">
            <el-tab-pane v-for="item in labelStructure" :key="item.value" :label="item.label" :name="item.value">
                <component :is="item.value" />
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script>
import structure from '../Attribute/structure/index.vue'
import settings from '../Attribute/settings/index.vue'
export default {
    name:'Attribute',
    
    components:{
        structure,
        settings
    },
    data() {
        return {
        labelStructure :[
        { label: '结构属性', value: 'structure' },
        { label: '设置', value: 'settings' }
    ],
    
    // 当前激活的标签
    activeLabelName:'structure',
        }
    },
    methods: {
        // getScene(){
        //     return this.$parent.$parent.$parent.$parent.getScene();
        // }
    },
    
  
}
</script>

<style lang="scss" scoped>
.Attribute {
    ::v-deep .el-tabs {
        margin: 8px;
        height: calc(100% - 16px);
        display: flex;
        flex-direction: column;
        .el-tabs__header {
            margin: 0;
            .el-tabs__nav {
                width: 100%;
                display: flex;
                .el-tabs__item {
                    height: 30px;
                    line-height: 30px;
                    flex: 1;
                    padding: 0;
                    text-align: center;
                    font-size: 12px;
                    background: #281928;
                    color: white;
                    &:nth-of-type(n + 3) {
                        border-left: 1px solid black;
                    }
                }
                .is-active {
                    color: #f300ff !important;
                }
                .el-tabs__active-bar {
                    background: rgba(243, 0, 255, 0.65);
                }
            }
        }
        .el-tabs__content {
            flex: 1;
            margin-top: 8px;
            overflow: hidden;
            .el-tab-pane {
                height: 100%;
                width: 100%;
            }
        }
    }
}
</style>