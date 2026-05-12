import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { TrendingDown, TrendingUp, Activity } from 'lucide-react';
import { PredictiveForecast } from '@/types/predict-lite';

interface VolatilityTrendCardLiteProps {
  forecast: PredictiveForecast | null;
  loading?: boolean;
}

export function VolatilityTrendCardLite({ forecast, loading }: VolatilityTrendCardLiteProps) {
  if (!forecast) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            Volatility Trend
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-muted-foreground text-sm">Loading...</div>
        </CardContent>
      </Card>
    );
  }

  const trendIcon = forecast.volatilityTrend === 'increasing'
    ? <TrendingUp className="w-4 h-4 text-red-600" />
    : forecast.volatilityTrend === 'decreasing'
      ? <TrendingDown className="w-4 h-4 text-green-600" />
      : <Activity className="w-4 h-4 text-blue-600" />;

  const trendColor = forecast.volatilityTrend === 'increasing'
    ? 'text-red-600'
    : forecast.volatilityTrend === 'decreasing'
      ? 'text-green-600'
      : 'text-blue-600';

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          Volatility Trend
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-center gap-3">
          {trendIcon}
          <span className={`text-lg font-semibold capitalize ${trendColor}`}>
            {forecast.volatilityTrend}
          </span>
        </div>

        <div className="text-sm text-muted-foreground">
          Expense variability compared to previous 30-day period
        </div>

        <div className="flex items-center gap-2">
          <div className="text-xs text-muted-foreground">Confidence:</div>
          <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden">
            <div
              className="h-full bg-blue-600"
              style={{ width: `${Math.round(forecast.confidence * 100)}%` }}
            />
          </div>
          <span className="text-xs font-medium">{Math.round(forecast.confidence * 100)}%</span>
        </div>
      </CardContent>
    </Card>
  );
}
