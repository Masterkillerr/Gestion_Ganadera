/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50:  '#f0faf0',
          100: '#dcf5dc',
          200: '#b5eab5',
          300: '#82d682',
          400: '#4eba4e',
          500: '#2d9c2d',
          600: '#1f7d1f',
          700: '#186118',
          800: '#154e15',
          900: '#113e11',
          950: '#071f07',
        },
        earth: {
          50:  '#fdf8f0',
          100: '#f9ecda',
          200: '#f0d4a8',
          300: '#e4b46a',
          400: '#d5903a',
          500: '#c27520',
          600: '#a45e18',
          700: '#844916',
          800: '#6b3c16',
          900: '#593314',
          950: '#2e1707',
        },
        dark: {
          900: '#0a0f0a',
          800: '#111711',
          700: '#182118',
          600: '#1e2b1e',
          500: '#253025',
          400: '#2e3d2e',
          300: '#3a4f3a',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
      },
    },
  },
  plugins: [],
}
