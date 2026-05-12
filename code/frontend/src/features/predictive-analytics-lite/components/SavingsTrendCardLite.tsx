import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { TrendingDown, TrendingUp } from 'lucide-react';
import { PredictiveForecast } from '@/types/predict-lite';

interface SavingsTrendCardLiteProps {
  forecast: PredictiveForecast | null;
  loading?: boolean;
}

export function SavingsTrendCardLite({ forecast, loading }: SavingsTrendCardLiteProps) {
  if (!forecast) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            Savings Trend
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-muted-foreground text-sm">Loading...</div>
        </CardContent>
      </Card>
    );
  }

  const trendIcon = forecast.savingsTrend === 'improving'
    ? <TrendingUp className="w-4 h-4 text-green-600" />
    : forecast.savingsTrend === 'declining'
      ? <TrendingDown className="w-4 h-4 text-red-600" />
      : null;

  const trendColor = forecast.savingsTrend === 'improving'
    ? 'text-green-600'
    : forecast.savingsTrend === 'declining'
      ? 'text-red-600'
      : 'text-amber-600';

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          Savings Trend
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-center gap-3">
          {trendIcon}
          <span className={`text-lg font-semibold capitalize ${trendColor}`}>
            {forecast.savingsTrend}
          </span>
        </div>

        <div className="text-sm text-muted-foreground">
          Net balance trajectory over recent 30-day periods
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
