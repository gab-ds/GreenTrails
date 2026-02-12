// This will be initialized by APP_INITIALIZER
// Services should inject ConfigService instead
export let API_BASE_URL: string = 'http://localhost:8080';

export function setApiBaseUrl(url: string): void {
  API_BASE_URL = url;
}
