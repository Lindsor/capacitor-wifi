export interface WifiPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
