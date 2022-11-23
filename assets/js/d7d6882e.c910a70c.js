"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[6061],{3905:(e,n,t)=>{t.d(n,{Zo:()=>p,kt:()=>c});var l=t(7294);function a(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function r(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);n&&(l=l.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,l)}return t}function i(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?r(Object(t),!0).forEach((function(n){a(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):r(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function u(e,n){if(null==e)return{};var t,l,a=function(e,n){if(null==e)return{};var t,l,a={},r=Object.keys(e);for(l=0;l<r.length;l++)t=r[l],n.indexOf(t)>=0||(a[t]=e[t]);return a}(e,n);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(l=0;l<r.length;l++)t=r[l],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(a[t]=e[t])}return a}var o=l.createContext({}),m=function(e){var n=l.useContext(o),t=n;return e&&(t="function"==typeof e?e(n):i(i({},n),e)),t},p=function(e){var n=m(e.components);return l.createElement(o.Provider,{value:n},e.children)},d={inlineCode:"code",wrapper:function(e){var n=e.children;return l.createElement(l.Fragment,{},n)}},s=l.forwardRef((function(e,n){var t=e.components,a=e.mdxType,r=e.originalType,o=e.parentName,p=u(e,["components","mdxType","originalType","parentName"]),s=m(t),c=a,k=s["".concat(o,".").concat(c)]||s[c]||d[c]||r;return t?l.createElement(k,i(i({ref:n},p),{},{components:t})):l.createElement(k,i({ref:n},p))}));function c(e,n){var t=arguments,a=n&&n.mdxType;if("string"==typeof e||a){var r=t.length,i=new Array(r);i[0]=s;var u={};for(var o in n)hasOwnProperty.call(n,o)&&(u[o]=n[o]);u.originalType=e,u.mdxType="string"==typeof e?e:a,i[1]=u;for(var m=2;m<r;m++)i[m]=t[m];return l.createElement.apply(null,i)}return l.createElement.apply(null,t)}s.displayName="MDXCreateElement"},3242:(e,n,t)=>{t.d(n,{Z:()=>a});var l=t(7294);const a=()=>l.createElement("p",null,l.createElement("span",{style:{backgroundColor:"#FC5D0D",borderRadius:"7px",color:"#fff",padding:"0.2rem",marginRight:"0.5rem"},title:"This feature is not part of the official DMN standard. It is an extension from Camunda's implementation."},"Camunda Extension"))},5485:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>m,contentTitle:()=>u,default:()=>s,frontMatter:()=>i,metadata:()=>o,toc:()=>p});var l=t(7462),a=(t(7294),t(3905)),r=t(3242);const i={id:"feel-built-in-functions-numeric",title:"Numeric functions",description:"This document outlines built-in numeric functions and examples."},u=void 0,o={unversionedId:"reference/builtin-functions/feel-built-in-functions-numeric",id:"reference/builtin-functions/feel-built-in-functions-numeric",title:"Numeric functions",description:"This document outlines built-in numeric functions and examples.",source:"@site/docs/reference/builtin-functions/feel-built-in-functions-numeric.md",sourceDirName:"reference/builtin-functions",slug:"/reference/builtin-functions/feel-built-in-functions-numeric",permalink:"/feel-scala/docs/next/reference/builtin-functions/feel-built-in-functions-numeric",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/docs/reference/builtin-functions/feel-built-in-functions-numeric.md",tags:[],version:"current",frontMatter:{id:"feel-built-in-functions-numeric",title:"Numeric functions",description:"This document outlines built-in numeric functions and examples."},sidebar:"Reference",previous:{title:"String functions",permalink:"/feel-scala/docs/next/reference/builtin-functions/feel-built-in-functions-string"},next:{title:"List functions",permalink:"/feel-scala/docs/next/reference/builtin-functions/feel-built-in-functions-list"}},m={},p=[{value:"decimal()",id:"decimal",level:2},{value:"floor()",id:"floor",level:2},{value:"ceiling()",id:"ceiling",level:2},{value:"round up()",id:"round-up",level:2},{value:"round down()",id:"round-down",level:2},{value:"round half up()",id:"round-half-up",level:2},{value:"round half down()",id:"round-half-down",level:2},{value:"abs()",id:"abs",level:2},{value:"modulo()",id:"modulo",level:2},{value:"sqrt()",id:"sqrt",level:2},{value:"log()",id:"log",level:2},{value:"exp()",id:"exp",level:2},{value:"odd()",id:"odd",level:2},{value:"even()",id:"even",level:2},{value:"random number()",id:"random-number",level:2}],d={toc:p};function s(e){let{components:n,...t}=e;return(0,a.kt)("wrapper",(0,l.Z)({},d,t,{components:n,mdxType:"MDXLayout"}),(0,a.kt)("h2",{id:"decimal"},"decimal()"),(0,a.kt)("p",null,"Round the given number at the given scale using the given rounding mode. If no rounding mode is passed in, it uses ",(0,a.kt)("inlineCode",{parentName:"p"},"HALF_EVEN")," as default."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"n"),": number"),(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"scale"),": number"),(0,a.kt)("li",{parentName:"ul"},"(optional) ",(0,a.kt)("inlineCode",{parentName:"li"},"mode"),": string - one of ",(0,a.kt)("inlineCode",{parentName:"li"},"UP, DOWN, CEILING, FLOOR, HALF_UP, HALF_DOWN, HALF_EVEN, UNNECESSARY")," (default: ",(0,a.kt)("inlineCode",{parentName:"li"},"HALF_EVEN"),")"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},'decimal(1/3, 2)\n// .33\n\ndecimal(1.5, 0) \n// 2\n\ndecimal(2.5, 0, "half_up")\n// 3\n')),(0,a.kt)("h2",{id:"floor"},"floor()"),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"n"),": number"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"floor(1.5)\n// 1\n\nfloor(-1.5)\n// -2\n\nfloor(-1.56, 1)\n// -1.6\n")),(0,a.kt)("h2",{id:"ceiling"},"ceiling()"),(0,a.kt)("p",null,"Round the given number at the given scale using the ceiling rounding mode."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"n"),": number\n-(optional) ",(0,a.kt)("inlineCode",{parentName:"li"},"scale"),": number (default: ",(0,a.kt)("inlineCode",{parentName:"li"},"0"),")"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"ceiling(1.5)\n// 2\n\nceiling(-1.5)\n// -1\n\nceiling(-1.56, 1)\n// -1.5\n")),(0,a.kt)("h2",{id:"round-up"},"round up()"),(0,a.kt)("p",null,"Round the given number at the given scale using the round-up rounding mode."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"n"),": number"),(0,a.kt)("li",{parentName:"ul"},"(optional) ",(0,a.kt)("inlineCode",{parentName:"li"},"scale"),": number (default: ",(0,a.kt)("inlineCode",{parentName:"li"},"0"),")"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"round up(5.5) \n// 6\n\nround up(-5.5)\n// -6\n\nround up(1.121, 2)\n// 1.13\n\nround up(-1.126, 2)\n// -1.13\n")),(0,a.kt)("h2",{id:"round-down"},"round down()"),(0,a.kt)("p",null,"Round the given number at the given scale using the round-down rounding mode."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"n"),": number"),(0,a.kt)("li",{parentName:"ul"},"(optional) ",(0,a.kt)("inlineCode",{parentName:"li"},"scale"),": number (default: ",(0,a.kt)("inlineCode",{parentName:"li"},"0"),")"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"round down(5.5)\n// 5\n\nround down (-5.5)\n// -5\n\nround down (1.121, 2)\n// 1.12\n\nround down (-1.126, 2)\n// -1.12\n")),(0,a.kt)("h2",{id:"round-half-up"},"round half up()"),(0,a.kt)("p",null,"Round the given number at the given scale using the round-half-up rounding mode."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"n"),": number"),(0,a.kt)("li",{parentName:"ul"},"(optional) ",(0,a.kt)("inlineCode",{parentName:"li"},"scale"),": number (default: ",(0,a.kt)("inlineCode",{parentName:"li"},"0"),")"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"round half up(5.5) \n// 6\n\nround half up(-5.5)\n// -6\n\nround half up(1.121, 2) \n// 1.12\n\nround half up(-1.126, 2)\n// -1.13\n")),(0,a.kt)("h2",{id:"round-half-down"},"round half down()"),(0,a.kt)("p",null,"Round the given number at the given scale using the round-half-down rounding mode."),(0,a.kt)("p",null,"-parameters:"),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"n"),": number\n-(optional) ",(0,a.kt)("inlineCode",{parentName:"li"},"scale"),": number (default: ",(0,a.kt)("inlineCode",{parentName:"li"},"0"),")"),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"round half down (5.5)\n// 5\n\nround half down (-5.5)\n// -5\n\nround half down (1.121, 2)\n// 1.12\n\nround half down (-1.126, 2)\n// -1.13\n")),(0,a.kt)("h2",{id:"abs"},"abs()"),(0,a.kt)("p",null,"Returns the absolute value of the given numeric value."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"number"),": number"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"abs(10)\n// 10\n\nabs(-10)\n// 10\n")),(0,a.kt)("h2",{id:"modulo"},"modulo()"),(0,a.kt)("p",null,"Returns the remainder of the division of dividend by divisor."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"dividend"),": number"),(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"divisor"),": number"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"modulo(12, 5)\n// 2\n")),(0,a.kt)("h2",{id:"sqrt"},"sqrt()"),(0,a.kt)("p",null,"Returns the square root."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"number"),": number"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"sqrt(16)\n// 4\n")),(0,a.kt)("h2",{id:"log"},"log()"),(0,a.kt)("p",null,"Returns the natural logarithm (base e) of the number."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"number"),": number"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"log(10)\n// 2.302585092994046\n")),(0,a.kt)("h2",{id:"exp"},"exp()"),(0,a.kt)("p",null,"Returns the Euler\u2019s number e raised to the power of number ."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"number"),": number"))),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"exp(5)\n// 148.4131591025766\n")),(0,a.kt)("h2",{id:"odd"},"odd()"),(0,a.kt)("p",null,"Returns ",(0,a.kt)("inlineCode",{parentName:"p"},"true")," if the given numeric value is odd. Otherwise, it returns ",(0,a.kt)("inlineCode",{parentName:"p"},"false"),"."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"number"),": number"))),(0,a.kt)("li",{parentName:"ul"},"result: boolean")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"odd(5)\n// true\n\nodd(2)\n// false\n")),(0,a.kt)("h2",{id:"even"},"even()"),(0,a.kt)("p",null,"Returns ",(0,a.kt)("inlineCode",{parentName:"p"},"true")," if the given numeric value is even. Otherwise, it returns ",(0,a.kt)("inlineCode",{parentName:"p"},"false"),"."),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters:",(0,a.kt)("ul",{parentName:"li"},(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("inlineCode",{parentName:"li"},"number"),": number"))),(0,a.kt)("li",{parentName:"ul"},"result: boolean")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"even(5)\n// false\n\neven(2)\n// true\n")),(0,a.kt)("h2",{id:"random-number"},"random number()"),(0,a.kt)(r.Z,{mdxType:"MarkerCamundaExtension"}),(0,a.kt)("p",null,"Returns a random number between ",(0,a.kt)("inlineCode",{parentName:"p"},"0")," and ",(0,a.kt)("inlineCode",{parentName:"p"},"1"),". "),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"parameters: none"),(0,a.kt)("li",{parentName:"ul"},"result: number")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-js"},"random number()\n// 0.9701618132579795\n")))}s.isMDXComponent=!0}}]);