"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[1215],{3905:(e,t,r)=>{r.d(t,{Zo:()=>d,kt:()=>f});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function a(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function l(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var c=n.createContext({}),p=function(e){var t=n.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):a(a({},t),e)),r},d=function(e){var t=p(e.components);return n.createElement(c.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},s=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,d=l(e,["components","mdxType","originalType","parentName"]),s=p(r),f=o,v=s["".concat(c,".").concat(f)]||s[f]||u[f]||i;return r?n.createElement(v,a(a({ref:t},d),{},{components:r})):n.createElement(v,a({ref:t},d))}));function f(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=r.length,a=new Array(i);a[0]=s;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l.mdxType="string"==typeof e?e:o,a[1]=l;for(var p=2;p<i;p++)a[p]=r[p];return n.createElement.apply(null,a)}return n.createElement.apply(null,r)}s.displayName="MDXCreateElement"},91:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>a,default:()=>u,frontMatter:()=>i,metadata:()=>l,toc:()=>p});var n=r(7462),o=(r(7294),r(3905));const i={id:"developer-guide-introduction",title:"Introduction",slug:"/developer-guide/"},a=void 0,l={unversionedId:"developer-guide/developer-guide-introduction",id:"version-1.19/developer-guide/developer-guide-introduction",title:"Introduction",description:"You can embed the FEEL engine in your application in different ways. Have a look",source:"@site/versioned_docs/version-1.19/developer-guide/developer-guide-introduction.md",sourceDirName:"developer-guide",slug:"/developer-guide/",permalink:"/feel-scala/docs/developer-guide/",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/versioned_docs/version-1.19/developer-guide/developer-guide-introduction.md",tags:[],version:"1.19",frontMatter:{id:"developer-guide-introduction",title:"Introduction",slug:"/developer-guide/"},sidebar:"Developer Guide",next:{title:"Bootstrapping",permalink:"/feel-scala/docs/developer-guide/bootstrapping"}},c={},p=[],d={toc:p};function u(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},d,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"You can embed the FEEL engine in your application in different ways. Have a look\nat ",(0,o.kt)("a",{parentName:"p",href:"/feel-scala/docs/developer-guide/bootstrapping"},"Bootstrapping")," to see how."),(0,o.kt)("p",null,"Afterward, you can extend and customize the FEEL engine by implementing one of the following\nSPIs (Service Provider Interface):"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("a",{parentName:"li",href:"/feel-scala/docs/developer-guide/function-provider-spi"},"Function Provider SPI")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("a",{parentName:"li",href:"/feel-scala/docs/developer-guide/value-mapper-spi"},"Value Mapper SPI")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("a",{parentName:"li",href:"/feel-scala/docs/developer-guide/clock-spi"},"Clock SPI"))))}u.isMDXComponent=!0}}]);