"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[6973],{3905:(t,e,r)=>{r.d(e,{Zo:()=>s,kt:()=>d});var n=r(7294);function o(t,e,r){return e in t?Object.defineProperty(t,e,{value:r,enumerable:!0,configurable:!0,writable:!0}):t[e]=r,t}function a(t,e){var r=Object.keys(t);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(t);e&&(n=n.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),r.push.apply(r,n)}return r}function i(t){for(var e=1;e<arguments.length;e++){var r=null!=arguments[e]?arguments[e]:{};e%2?a(Object(r),!0).forEach((function(e){o(t,e,r[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(r,e))}))}return t}function l(t,e){if(null==t)return{};var r,n,o=function(t,e){if(null==t)return{};var r,n,o={},a=Object.keys(t);for(n=0;n<a.length;n++)r=a[n],e.indexOf(r)>=0||(o[r]=t[r]);return o}(t,e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(t);for(n=0;n<a.length;n++)r=a[n],e.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(t,r)&&(o[r]=t[r])}return o}var u=n.createContext({}),c=function(t){var e=n.useContext(u),r=e;return t&&(r="function"==typeof t?t(e):i(i({},e),t)),r},s=function(t){var e=c(t.components);return n.createElement(u.Provider,{value:e},t.children)},p={inlineCode:"code",wrapper:function(t){var e=t.children;return n.createElement(n.Fragment,{},e)}},f=n.forwardRef((function(t,e){var r=t.components,o=t.mdxType,a=t.originalType,u=t.parentName,s=l(t,["components","mdxType","originalType","parentName"]),f=c(r),d=o,m=f["".concat(u,".").concat(d)]||f[d]||p[d]||a;return r?n.createElement(m,i(i({ref:e},s),{},{components:r})):n.createElement(m,i({ref:e},s))}));function d(t,e){var r=arguments,o=e&&e.mdxType;if("string"==typeof t||o){var a=r.length,i=new Array(a);i[0]=f;var l={};for(var u in e)hasOwnProperty.call(e,u)&&(l[u]=e[u]);l.originalType=t,l.mdxType="string"==typeof t?t:o,i[1]=l;for(var c=2;c<a;c++)i[c]=r[c];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},5085:(t,e,r)=>{r.r(e),r.d(e,{assets:()=>u,contentTitle:()=>i,default:()=>p,frontMatter:()=>a,metadata:()=>l,toc:()=>c});var n=r(7462),o=(r(7294),r(3905));const a={id:"tutorial-1-1",title:"1.1 First stop: Numeric functions"},i=void 0,l={unversionedId:"tutorial/tutorial-1-1",id:"tutorial/tutorial-1-1",title:"1.1 First stop: Numeric functions",description:"Let's start our quest. FEEL allows you to use basic calculations like addition, subtraction and multiplication to name a few. Our friend arrived in Spain by boat, specifically Cadiz. The goal is to reach Pamplona (which is 1,030.8 kms away).",source:"@site/docs/tutorial/tutorial-1-1.md",sourceDirName:"tutorial",slug:"/tutorial/tutorial-1-1",permalink:"/feel-scala/docs/tutorial/tutorial-1-1",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/master/docs/docs/tutorial/tutorial-1-1.md",tags:[],version:"current",frontMatter:{id:"tutorial-1-1",title:"1.1 First stop: Numeric functions"},sidebar:"Tutorial",previous:{title:"Introduction",permalink:"/feel-scala/docs/tutorial/tutorial-1"},next:{title:"1.2 Second stop: More numeric functions",permalink:"/feel-scala/docs/tutorial/tutorial-1-2"}},u={},c=[],s={toc:c};function p(t){let{components:e,...r}=t;return(0,o.kt)("wrapper",(0,n.Z)({},s,r,{components:e,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"Let's start our quest. FEEL allows you to use basic calculations like addition, subtraction and multiplication to name a few. Our friend arrived in Spain by boat, specifically Cadiz. The goal is to reach Pamplona (which is 1,030.8 kms away). "),(0,o.kt)("p",null,"As part of the quest, Camundonaut received magical items and decided to use The Boots of Hermes, which give its wearer a speed of 48.2 kms/hour."),(0,o.kt)("p",null,"Using numeric functions, how long would it take him to get there? Consider resting for 30 minutes every 5 hours (Let's also round up the number for total resting time)"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre"},"// Formula considering resting time plus total time\n\nround up(.5*(1030.8/48.2)/5,0) + 1030.8/48.2\n\n")))}p.isMDXComponent=!0}}]);