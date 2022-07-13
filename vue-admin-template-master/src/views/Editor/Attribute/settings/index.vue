<template>
    <div class="settings">
        <el-scrollbar class="Scrollbar">
       
            <div class="Panel">
                <div class="Row">
                    <span class="Title">视窗</span>
                </div>
                <div class="Row">
                    <span class="Text">网格板</span>
                    <span class="Check">
                        <el-switch v-model="state.GridHelper" active-color="#f300ff"></el-switch>
                    </span>  
                </div>
                 <div class="Row">
                    <span class="Text">坐标轴</span>
                    <span class="Check">
                        <el-switch v-model="state.AxesHelper" active-color="#f300ff" ></el-switch>
                    </span>    
                </div>
                <div class="Row">
                    <span class="Text">光线辅助</span>
                    <span class="Check">
                        <el-switch v-model="state.LightHelper" active-color="#f300ff" ></el-switch> 
                    </span>    
                </div>
                 <div class="Row">
                    <span class="Text">平面辅助</span>
                
                    <span class="Check">
                        <el-switch v-model="state.PlaneHelper" active-color="#f300ff"></el-switch>
                    </span>    
                </div>
                <div class="Row">
                    <span class="Text">包装盒辅助</span>
                    <span class="Check">
                        <el-switch v-model="state.BoxHelper" active-color="#f300ff"></el-switch>
                    </span>    
                </div>
                <div class="Row">
                    <span class="Text">控制器</span>
                    <span class="Check">
                        <el-switch v-model="state.Control" active-color="#f300ff"></el-switch>
                    </span>    
                </div>
            </div>
            <div class="Panel">
                <div class="Row">
                    <span class="Title">帮助</span>
                </div>
                <div class="Row">
                    <span class="Text">平移控件</span>
                    <div class="Help">
                        <span class="Cell">
                            <i class="Key">T</i>
                        </span>
                    </div>
                </div>
                <div class="Row">
                    <span class="Text">旋转控件</span>
                     <div class="Help">
                        <span class="Cell">
                            <i class="Key">R</i>
                        </span>
                    </div>
                </div>
                <div class="Row">
                    <span class="Text">缩放控件</span>
                    <div class="Help">
                        <span class="Cell">
                            <i class="Key">E</i>
                        </span>
                    </div>
                </div>
                <div class="Row">
                    <span class="Text">工具操作键</span>
                    <div class="Help">
                        <svg-icon icon-class="mouseL" className="Mouse"/>
                    </div>
                </div> 
                
                <div class="Row">
                    <span class="Text">相机旋转</span>
                    <div class="Help">
                       <svg-icon icon-class="mouseR" className="Mouse"/>
                    </div>
                </div>
                <div class="Row">
                    <span class="Text">相机平移</span>
                     <div class="Help">
                        <span class="Cellctrl">
                            <i class="Key">CTRL</i> 
                        </span>
                       
                    </div>
                    <div class="MCHelp">
                       <svg-icon icon-class="mouseR" className="Mouse"/>
                    </div>
                </div>
                <div class="Row">
                    <span class="Text">相机缩放</span>
                     <div class="Help">
                       <svg-icon icon-class="mouseM" className="Mouse"/>
                    </div>
                </div>
                 <div class="Row">
                    <span class="Text">删除</span>
                     <div class="Help">
                        <span class="BsCell">
                            <i class="Key">Backspace</i>
                        </span>
                    </div>
                </div>
                 <div class="Row">
                    <span class="Text">撤销</span>
                     <div class="Help">
                       <span class="Cellctrl">
                            <i class="Key">CTRL</i>
                        </span>
                        <span class="Cell">
                            <i class="Key">z</i>
                        </span>
                    </div>
                </div>
                 <div class="Row">
                    <span class="Text">反撤销</span>
                     <div class="Help">
                       <span class="Cellctrl">
                            <i class="Key">CTRL</i>
                        </span>
                        <span class="Cell">
                            <i class="Key">Y</i>
                        </span>
                    </div>
                </div>
                 <div class="Row">
                    <span class="Text">保存</span>
                    <div class="Help">
                        <span class="Cellctrl">
                            <i class="Key">CTRL</i>
                        </span>
                        <span class="Cell">
                            <i class="KeyALT">ALT</i>
                        </span>
                         <span class="Cell">
                            <i class="Key">S</i>
                        </span>
                    </div>
                </div>
            </div>
            <div class="Panel">
                <div class="Row">
                    <span class="Title">历史</span>
                    <span class="Current">
                        <el-checkbox></el-checkbox>
                        <span class="Text">当前操作</span>
                    </span>
                     <br class="Break">
                <br class="Break">
                <div class="Outliner" tabindex="0">
                </div>
            </div>
               
        </div>
         </el-scrollbar>
    </div>
</template>

<script>

export default {
    
    name:'settings',
    data() {
        return {
            i:true,
            state:{
                GridHelper:true,
                AxesHelper:true,
                LightHelper:true,
                PlaneHelper:false,
                BoxHelper:false,
                Control:true
            }
        }
    },
    // watch:{
    //     sate:{
    //         handler(){
    //             this.$bus.$emit('stateControl',this.sate);
    //         },
    //         deep:true,
    //         immediate:true
    //     }
    // },
    methods: {
        updateHelper(){
            let that=this;
            this.$bus.$on('stateControl',(state)=>{
                that.state=state;
            })
        },
    },
    mounted() {
        this.updateHelper();
    },
}
</script>

<style lang="scss" scoped>
.settings{
    height:530px;
    width: 100%;
    background: #281928;
    overflow-y : auto;
   

    
    .Panel{
        border-top: 0px;
        padding-top: 20px;
        display: block; 
        border-bottom: 4px solid #432d43;
        .Row{
                display: flex;
                align-items: center;
                min-height: 24px;
                margin-bottom: 10px;
                .Check{
                    margin-right: 4px;
                     position: relative; 
                     left:60px;
                }
                .Title{
                    cursor: default;
                    display: inline-block;
                    text-indent:20px;
                    width: 90px;
                    color: white;
                    font-size: 20px;
                    font-weight: bolder;
                }
                .Current{
                    margin-right: 4px; 
                    position: relative; 
                    left: 20px;
                }
                .Text{
                     cursor: default;
                    display: inline-block;
                    text-indent:20px;
                    width: 90px;
                    color: white;
                    font-size: 13px;
                }
                .MCHelp{
                   position: relative;
                
                   .Mouse{
                            height: 30px;
                            width: 30px;
                            position: relative;
                           display: inline-block;
                        }  
                }
                 
                .Help{
                    position: relative;
                    
                     .Mouse{
                            height: 30px;
                            width: 30px;
                            position: relative;
                           display: inline-block;
                        }  
                    .Cell{ 
                    position: relative;
                    display: inline-block;
                    width: 30px;
                    height: 30px;
                    padding: 3px 6px;
                    margin:6px 3px;
                    border-radius: 4px;
                    background: linear-gradient(180deg,#282828,#202020);
                    box-shadow: inset -3px 0 3px rgba(0,0,0,0.15),
                    inset 0 -3px 3px rgba(0,0,0,0.25),
                    0 0 0 1.5px rgba(0,0,0,0.75),
                    3.75px 7.5px 13px rgba($color: #000000, $alpha: 0.4);
                    overflow: hidden;
                    .KeyALT{
                        position: relative;
                        color: #fff;
                        font-style: normal;
                        text-transform: uppercase;
                        font-size: 10px;
                    }
                    .Key{
                        position: relative;
                        color: #fff;
                        font-style: normal;
                        text-transform: uppercase;
                    }
                }
                .Cell::before{
                    content: '';
                    position: absolute;
                    top: 1.25px;
                    left: 1.5px;
                    bottom:5px;
                    right:4.5px;
                    background: linear-gradient(90deg,#232323,#4a4a4a);
                    border-radius: 3.75px;
                    box-shadow: -3.75px -3.75px 3.75px rgba(255,255,255,0.25),
                    3.75px 1.8px 3.75px rgba(0,0,0,0.15);
                    border-left: 1px solid #0004;
                    border-bottom: 1px solid #0004;
                    border-top:1px solid #0009;
                }
                .Cellctrl{ 
                    position: relative;
                    display: inline-block;
                    width: 50px;
                    height: 30px;
                    padding: 3px 6px;
                    margin:6px 3px;
                    border-radius: 4px;
                    background: linear-gradient(180deg,#282828,#202020);
                    box-shadow: inset -3px 0 3px rgba(0,0,0,0.15),
                    inset 0 -3px 3px rgba(0,0,0,0.25),
                    0 0 0 1.5px rgba(0,0,0,0.75),
                    3.75px 7.5px 13px rgba($color: #000000, $alpha: 0.4);
                    overflow: hidden;
                    .Key{
                        position: relative;
                        color: #fff;
                        font-style: normal;
                        text-transform: uppercase;
                        font-size: 10px;
                    }
                }
                .Cellctrl::before{
                    content: '';
                    position: absolute;
                    top: 1.25px;
                    left: 1.5px;
                    bottom:5px;
                    right:4.5px;
                    background: linear-gradient(90deg,#232323,#4a4a4a);
                    border-radius: 3.75px;
                    box-shadow: -3.75px -3.75px 3.75px rgba(255,255,255,0.25),
                    3.75px 1.8px 3.75px rgba(0,0,0,0.15);
                    border-left: 1px solid #0004;
                    border-bottom: 1px solid #0004;
                    border-top:1px solid #0009;
                }
                 .BsCell{ 
                    position: relative;
                    display: inline-block;
                    width: 90px;
                    height: 30px;
                    padding: 3px 6px;
                    margin:6px 3px;
                    border-radius: 4px;
                    background: linear-gradient(180deg,#282828,#202020);
                    box-shadow: inset -3px 0 3px rgba(0,0,0,0.15),
                    inset 0 -3px 3px rgba(0,0,0,0.25),
                    0 0 0 1.5px rgba(0,0,0,0.75),
                    3.75px 7.5px 13px rgba($color: #000000, $alpha: 0.4);
                    overflow: hidden;
                    .Key{
                        position: relative;
                        color: #fff;
                        font-style: normal;
                        text-transform: uppercase;
                        font-size: 10px;
                    }
                }
                .BsCell::before{
                    content: '';
                    position: absolute;
                    top: 1.25px;
                    left: 1.5px;
                    bottom:5px;
                    right:4.5px;
                    background: linear-gradient(90deg,#232323,#4a4a4a);
                    border-radius: 3.75px;
                    box-shadow: -3.75px -3.75px 3.75px rgba(255,255,255,0.25),
                    3.75px 1.8px 3.75px rgba(0,0,0,0.15);
                    border-left: 1px solid #0004;
                    border-bottom: 1px solid #0004;
                    border-top:1px solid #0009;
                }

                }
                
                
            }
    }
    
   }
</style>
<style lang="scss">
.Scrollbar {
  height: 100%;
  width: 100%;
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  .el-scrollbar__wrap {
    // 横竖滚动条都直接显示出来，height加上17px把滚动条隐藏掉
    // width已在el-scrollbar内部样式处理了
    // overflow: scroll; // 不能设置为overflow-x:hidden,滚动条会重叠
    // height: calc(100% + 17px);
    // width: calc(100% + 17px);
    // 使用margin负值方案在移动端会出现高度、宽度异常问题，改为以下方案
    height: 100%;
    overflow: auto;
    margin: 0 !important;
    // 火狐
    scrollbar-width: none !important;
    // IE
    -ms-overflow-style: none;
    // 谷歌
    &::-webkit-scrollbar {
      display: none !important;
    }
    &::-webkit-scrollbar {
      display: none;
      width: 0 !important;
    }
    &.el-scrollbar__wrap--hidden-default {
      height: 100% !important;
    }
    
  }
  > .el-scrollbar__bar {
    z-index: 1999;
    
  }
  .el-scrollbar__thumb {
    background: #995f99;
    transition: background 0.4s;
  }
  .el-scrollbar__thumb:hover {
    background: #c98bc9;
  }
}

</style>