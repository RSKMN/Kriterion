import { useEffect, useState } from 'react';
import { RefreshCw } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PredictiveForecast } from '@/types/predict-lite';
import { predictLiteService } from '@/services/api/predict-lite.service';
import { OverspendingForecastCardLite } from '@/features/predictive-analytics-lite/components/OverspendingForecastCardLite';
import { SavingsTrendCardLite } from '@/features/predictive-analytics-lite/components/SavingsTrendCardLite';
import { VolatilityTrendCardLite } from '@/features/predictive-analytics-lite/components/VolatilityTrendCardLite';
import { SubscriptionPressureForecastCardLite } from '@/features/predictive-analytics-lite/components/SubscriptionPressureForecastCardLite';
import { EndOfMonthRiskCardLite } from '@/features/predictive-analytics-lite/components/EndOfMonthRiskCardLite';
import { PredictiveReflectionsLite } from '@/features/predictive-analytics-lite/components/PredictiveReflectionsLite';

export default function PredictiveAnalyticsLitePage() {
  const [loading, setLoading] = useState(true);
  const [seedLoading, setSeedLoading] = useState(false);
  const [forecast, setForecast] = useState<PredictiveForecast | null>(null);

  const loadData = async () => {
    setLoading(true);
    try {
      const [forecastResult] = await Promise.allSettled([
        predictLiteService.getForecast(),
      ]);

      if (forecastResult.status === 'fulfilled') {
        setForecast(forecastResult.value);
      }
    } catch (error) {
      console.error('Failed to load predictive analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  const seedDemoData = async () => {
    setSeedLoading(true);
    try {
      await predictLiteService.seedDemoData();
      // Reload data after seeding
      await loadData();
    } catch (error) {
      console.error('Failed to seed demo data:', error);
    } finally {
      setSeedLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const dataStatus = forecast?.dataStatus || 'loading';
  const statusLabel =
    dataStatus === 'ready' ? 'Ready' : dataStatus === 'partial' ? 'Partial Data' : 'Insufficient Data';
  const statusColor =
    dataStatus === 'ready' ? 'bg-green-100 text-green-800' : 'bg-amber-100 text-amber-800';

  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold">Predictive Analytics</h1>
            <p className="text-muted-foreground mt-1">
              Heuristic-based spending forecasts and financial trend analysis
            </p>
          </div>
          <div className="flex items-center gap-4">
            <div className={`px-3 py-1 rounded-full text-xs font-medium ${statusColor}`}>
              {statusLabel}
            </div>
            <Button
              onClick={seedDemoData}
              disabled={seedLoading || loading}
              variant="outline"
              size="sm"
              className="gap-2"
            >
              <RefreshCw className={`w-4 h-4 ${seedLoading ? 'animate-spin' : ''}`} />
              Seed Data
            </Button>
            <Button
              onClick={loadData}
              disabled={loading}
              variant="outline"
              size="sm"
              className="gap-2"
            >
              <RefreshCw className="w-4 h-4" />
              Refresh
            </Button>
          </div>
        </div>

        {/* Main Content Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* Row 1 */}
          <OverspendingForecastCardLite forecast={forecast} loading={loading} />
          <SavingsTrendCardLite forecast={forecast} loading={loading} />
          <VolatilityTrendCardLite forecast={forecast} loading={loading} />

          {/* Row 2 */}
          <SubscriptionPressureForecastCardLite forecast={forecast} loading={loading} />
          <EndOfMonthRiskCardLite forecast={forecast} loading={loading} />

          {/* Row 3 - Full width */}
          <div className="md:col-span-2 lg:col-span-3">
            <PredictiveReflectionsLite forecast={forecast} loading={loading} />
          </div>
        </div>

        {/* Footer */}
        <div className="mt-8 p-4 rounded-lg bg-muted/50 border border-muted-foreground/10">
          <p className="text-xs text-muted-foreground">
            <strong>Predictive Models:</strong> This system uses time-series heuristics including
            moving averages, volatility analysis, trend detection, and anomaly identification. All
            forecasts are based on historical spending patterns and transaction data from your
            account.
          </p>
        </div>
      </div>
    </div>
  );
}
