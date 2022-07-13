<template>
   <div class="baseElement">
      <el-scrollbar class="Scrollbar">
        <el-collapse :accordion="false" v-model="activeNames">
            <el-collapse-item v-for="(item, index) in renderList" :key="index" :name="index">
                <template #title>
                    <header>
                        <i :class="activeNames === index ? 'el-icon-caret-bottom' : 'el-icon-caret-right'" />
                        <i :class="item.icon" />
                        <span>{{ item.name }}</span>
                        <!-- <label v-if="item.import">
                            <el-tooltip class="item" effect="light" :content="item.importTitle" placement="left">
                               <svg-icon style="outline: unset" @click.stop="item.import(item)" iconClass="icon-import" className="icon-import" />
                            </el-tooltip>
                        </label> -->
                    </header>
                </template>
                <div class="item" v-for="(em, indexOf) in item.children" :key="indexOf">
                    <article @click="control(em, item)"><i :class="item.icon" /></article>
                    <span>{{ em.name }}</span>
                </div>
            </el-collapse-item>
        </el-collapse>
      </el-scrollbar>
    </div>
</template>

<script>
export default {
    name:'baseElement',
    props:['renderList'],
    data() {
        return {
            activeNames:''
        }
    },
    methods: {
        control(em, item){
            // this.$parent.fatherControl(em,item);
            // console.log(this.$parent.$parent.$parent);
            //this.$parent.$parent.$parent.fatherControl(em,item);
            this.$bus.$emit('AddObjects',em,item);



        }
    },

}
</script>

<style lang="scss" scoped>
.baseElement {
    height: 100%;
    width: 100%;
    overflow-y: auto;
    ::v-deep .el-collapse {
        border: none;
        .el-collapse-item {
            margin-bottom: 10px;
            background: #281928;
            border: none;
            .el-collapse-item__header,
            .el-collapse-item__wrap {
                background: unset;
                color: white;
                border: none;
            }
            .el-collapse-item__header {
                height: 40px;
                > i {
                    display: none;
                }
                header {
                    width: 100%;
                    padding: 0 10px;
                    position: relative;
                    i {
                        &:nth-of-type(2) {
                            color: #c50eff;
                            margin: 0 4px;
                        }
                    }

                    label {
                        position: absolute;
                        right: 10px;
                        .icon-import {
                            color: #f300ff;
                            cursor: alias;
                            &:hover {
                                color: white;
                            }
                        }
                    }
                }
            }
            .el-collapse-item__content {
                padding: 0 10px 10px 10px;
                .item {
                    margin-bottom: 10px;
                    cursor: pointer;
                    float: left;
                    width: calc(33% - 6px);
                    height: 90px;
                    color: white;
                    &:nth-of-type(3n + 3) {
                        margin-right: unset;
                    }
                    margin-right: 10px;
                    article {
                        background: #432d43;
                        height: 60px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        &:hover {
                            background: #ffa7fb;
                        }
                    }
                    span {
                        margin-top: 6px;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                    }
                }
            }
            &:last-of-type {
                margin-bottom: unset;
            }
        }
    }
}
</style>
