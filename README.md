# Form Error Repro

Minimal reproduction of strange semantic ui form input.

## Usage

```
npm install
npx shadow-cljs watch :dev
```

With these done, you should be able to go to `http://localhost:8000` to view a tiny form. There are two versions of the input element defined in `src/form_error_repro/core.cljs` which have comments explaining which works and which does not. The input should be invalid when blank and valid otherwise. The broken component retains its error state even after the input has characters in it again.
